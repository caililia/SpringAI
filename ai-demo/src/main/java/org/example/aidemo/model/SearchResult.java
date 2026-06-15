package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * RAG 知识库 - 搜索结果模型
 */
public class SearchResult {
    private String id;
    private String documentId;
    private String content;
    private Double score;        // 相似度分数
    private String source;       // 来源文档
    private LocalDateTime createdAt;

    public SearchResult() {
        this.createdAt = LocalDateTime.now();
    }

    public SearchResult(String documentId, String content, Double score) {
        this();
        this.documentId = documentId;
        this.content = content;
        this.score = score;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
