package org.example.aidemo.assistant;

import org.example.aidemo.model.Conversation;
import org.example.aidemo.service.ConversationService;
import org.example.aidemo.service.ImageService;
import org.example.aidemo.service.SpeechService;
import org.example.aidemo.service.ToolService;
import org.example.aidemo.service.WebsiteAssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * AI 网站助手控制器
 * 实现智能问答、工具调用、流式响应、文生图、语音合成等功能
 */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final WebsiteAssistantService websiteAssistantService;
    private final ConversationService conversationService;
    private final ImageService imageService;
    private final SpeechService speechService;
    private final ToolService toolService;
    private final ChatClient dashscopeChatClient;
    private final ChatClient ollamaChatClient;

    public AssistantController(
            WebsiteAssistantService websiteAssistantService,
            ConversationService conversationService,
            ImageService imageService,
            SpeechService speechService,
            ToolService toolService,
            ChatClient dashscopeChatClient,
            ChatClient ollamaChatClient) {
        this.websiteAssistantService = websiteAssistantService;
        this.conversationService = conversationService;
        this.imageService = imageService;
        this.speechService = speechService;
        this.toolService = toolService;
        this.dashscopeChatClient = dashscopeChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    // ==================== 基础聊天（无会话） ====================

    /**
     * 基础聊天（默认模型）
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = websiteAssistantService.chat(message);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * 带系统提示的聊天
     */
    @PostMapping("/chat/with-system")
    public ResponseEntity<Map<String, String>> chatWithSystem(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有用的AI助手。");
        String response = websiteAssistantService.chatWithSystem(message, systemPrompt);
        return ResponseEntity.ok(Map.of(
            "response", response,
            "systemPrompt", systemPrompt
        ));
    }

    /**
     * DashScope 模型聊天
     */
    @PostMapping("/dashscope/chat")
    public ResponseEntity<Map<String, String>> dashscopeChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有用的AI助手。");
        String response = dashscopeChatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * Ollama 本地模型聊天
     */
    @PostMapping("/ollama/chat")
    public ResponseEntity<Map<String, String>> ollamaChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有用的AI助手。");
        String response = ollamaChatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * Ollama 本地模型流式聊天
     */
    @PostMapping("/ollama/stream")
    public Flux<String> ollamaChatStream(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return ollamaChatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    // ==================== 会话管理 ====================

    /**
     * 获取所有会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getAllConversations() {
        List<Conversation> conversations = conversationService.getAllConversations();
        return ResponseEntity.ok(conversations);
    }

    /**
     * 创建新会话
     */
    @PostMapping("/conversation")
    public ResponseEntity<Map<String, String>> createConversation() {
        Conversation conv = conversationService.createConversation("dashscope");
        return ResponseEntity.ok(Map.of(
            "conversationId", conv.getId(),
            "model", conv.getModel()
        ));
    }

    /**
     * 多轮对话（带历史）
     */
    @PostMapping("/conversation/{conversationId}/chat")
    public ResponseEntity<Map<String, Object>> multiRoundChat(
            @PathVariable String conversationId,
            @RequestBody Map<String, String> request) {

        Conversation conv = conversationService.getConversation(conversationId);
        if (conv == null) {
            return ResponseEntity.notFound().build();
        }

        String userMessage = request.get("message");
        conversationService.addUserMessage(conversationId, userMessage);

        // 构建历史消息
        StringBuilder historyPrompt = new StringBuilder();
        for (var msg : conv.getMessages()) {
            if ("user".equals(msg.getRole())) {
                historyPrompt.append("用户: ").append(msg.getContent()).append("\n");
            } else {
                historyPrompt.append("助手: ").append(msg.getContent()).append("\n");
            }
        }

        String response = websiteAssistantService.chat(historyPrompt.toString());
        conversationService.addAssistantMessage(conversationId, response);

        return ResponseEntity.ok(Map.of(
            "response", response,
            "conversationId", conversationId
        ));
    }

    /**
     * 获取会话历史
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String conversationId) {
        Conversation conv = conversationService.getConversation(conversationId);
        if (conv == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conv);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String conversationId) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 文生图 ====================

    /**
     * 文生图
     */
    @PostMapping("/image/generate")
    public ResponseEntity<Map<String, String>> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String imageUrl = imageService.generateImage(prompt);
        return ResponseEntity.ok(Map.of(
            "prompt", prompt,
            "imageUrl", imageUrl
        ));
    }

    /**
     * 文生图（优化提示词）
     */
    @PostMapping("/image/generate/enhanced")
    public ResponseEntity<Map<String, String>> generateImageEnhanced(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String imageUrl = imageService.generateImageWithOptimization(prompt);
        return ResponseEntity.ok(Map.of(
            "prompt", prompt,
            "imageUrl", imageUrl
        ));
    }

    // ==================== 语音合成 ====================

    /**
     * 文本转语音（返回音频文件）
     */
    @PostMapping("/speech/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String voice = request.get("voice");
        Double speed = request.containsKey("speed") ? Double.parseDouble(request.get("speed")) : null;

        byte[] audioBytes = speechService.textToSpeech(text, voice, speed);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("speech.mp3")
                .build());

        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }

    // ==================== 工具调用 ====================

    /**
     * 天气查询
     */
    @GetMapping("/tools/weather")
    public ResponseEntity<Map<String, Object>> queryWeather(@RequestParam String city) {
        String result = toolService.queryWeather(city);
        return ResponseEntity.ok(Map.of(
            "city", city,
            "result", result
        ));
    }

    /**
     * 计算器
     */
    @GetMapping("/tools/calculate")
    public ResponseEntity<Map<String, Object>> calculate(@RequestParam String expression) {
        String result = toolService.calculate(expression);
        return ResponseEntity.ok(Map.of(
            "expression", expression,
            "result", result
        ));
    }

    /**
     * 智能工具调用
     */
    @PostMapping("/tools/smart")
    public ResponseEntity<Map<String, Object>> smartToolCall(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        String result = toolService.smartToolCall(userInput);
        return ResponseEntity.ok(Map.of(
            "input", userInput,
            "result", result
        ));
    }

    // ==================== 网站助手 ====================

    /**
     * 网站助手问答
     */
    @PostMapping("/website/chat")
    public ResponseEntity<Map<String, String>> websiteChat(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String response = websiteAssistantService.handleWebsiteQuery(query);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * 流式对话
     */
    @PostMapping("/chat/stream")
    public Flux<String> chatStream(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return websiteAssistantService.chatStream(message);
    }
}
