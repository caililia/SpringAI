package org.example.aidemo.rag;

import org.example.aidemo.model.KnowledgeDocument;
import org.example.aidemo.model.SearchResult;
import org.example.aidemo.service.KnowledgeBaseService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RAG 知识库控制器
 * 提供文档上传、知识搜索、智能问答接口
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final ChatClient defaultClient;

    public RagController(KnowledgeBaseService knowledgeBaseService,
                         @Qualifier("defaultChatClient") ChatClient defaultClient) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.defaultClient = defaultClient;
    }

    /**
     * 上传文档（文本）
     */
    @PostMapping("/document")
    public ResponseEntity<KnowledgeDocument> uploadDocument(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");

        KnowledgeDocument doc = new KnowledgeDocument(title, content, "text");
        doc.setId(UUID.randomUUID().toString());
        doc.setStatus("completed");

        knowledgeBaseService.addDocument(content);

        return ResponseEntity.ok(doc);
    }

    /**
     * 上传文件
     */
    @PostMapping("/document/upload")
    public ResponseEntity<KnowledgeDocument> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String content = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .reduce("", (a, b) -> a + "\n" + b);

            KnowledgeDocument doc = new KnowledgeDocument();
            doc.setId(UUID.randomUUID().toString());
            doc.setTitle(file.getOriginalFilename());
            doc.setContent(content);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(getFileExtension(file.getOriginalFilename()));
            doc.setFileSize(file.getSize());
            doc.setStatus("completed");

            knowledgeBaseService.addDocument(content);

            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 知识搜索
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@RequestParam String query) {
        List<String> docs = knowledgeBaseService.search(query);
        List<SearchResult> results = new ArrayList<>();

        for (int i = 0; i < docs.size(); i++) {
            SearchResult result = new SearchResult();
            result.setId("result_" + i);
            result.setContent(docs.get(i));
            result.setScore(1.0 - i * 0.1);
            result.setSource("knowledge_base");
            results.add(result);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * 智能问答（RAG）
     */
    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        List<String> relevantDocs = knowledgeBaseService.search(question);

        if (relevantDocs.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "answer", "抱歉，知识库中没有找到相关信息。",
                "sources", List.of(),
                "success", true
            ));
        }

        // 构建 RAG 提示词
        StringBuilder context = new StringBuilder();
        context.append("请根据以下参考资料回答问题：\n\n");
        for (int i = 0; i < relevantDocs.size(); i++) {
            context.append(String.format("[%d] %s\n\n", i + 1, relevantDocs.get(i)));
        }
        context.append("问题：").append(question);

        String answer = defaultClient.prompt()
                .user(context.toString())
                .call()
                .content();

        return ResponseEntity.ok(Map.of(
            "answer", answer,
            "sources", relevantDocs,
            "success", true
        ));
    }

    /**
     * 获取知识库统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
            "documentCount", knowledgeBaseService.getDocumentCount(),
            "timestamp", LocalDateTime.now()
        ));
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
