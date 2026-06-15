package org.example.aidemo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * AI 网站助手服务
 * 实现智能问答、工具调用、流式响应等功能
 */
@Service
public class WebsiteAssistantService {

    private final ChatClient defaultClient;
    private final ToolService toolService;

    public WebsiteAssistantService(
            @Qualifier("defaultChatClient") ChatClient defaultClient,
            ToolService toolService) {
        this.defaultClient = defaultClient;
        this.toolService = toolService;
    }

    /**
     * 智能问答 - 同步模式
     */
    public String chat(String userMessage) {
        return defaultClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 智能问答 - 流式模式
     */
    public reactor.core.publisher.Flux<String> chatStream(String userMessage) {
        return defaultClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

    /**
     * 带系统提示的问答
     */
    public String chatWithSystem(String userMessage, String systemPrompt) {
        return defaultClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 带工具调用的智能问答
     */
    public String chatWithTools(String userMessage) {
        // 先判断是否需要调用工具
        String toolResult = toolService.smartToolCall(userMessage);

        // 构建最终回答
        String prompt = String.format(
            "用户问题：%s\n\n" +
            "工具调用结果：%s\n\n" +
            "请基于工具调用结果，给出完整、友好的回答。",
            userMessage, toolResult
        );

        return defaultClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 网站助手 - 处理常见问题
     */
    public String handleWebsiteQuery(String query) {
        String systemPrompt = """
            你是网站智能助手，负责回答用户关于网站功能、使用方法的咨询。
            
            你的职责：
            1. 回答关于网站功能的问题
            2. 指导用户如何使用各项功能
            3. 处理用户反馈和建议
            
            请用友好、专业的语气回答。
            """;

        return chatWithSystem(query, systemPrompt);
    }
}
