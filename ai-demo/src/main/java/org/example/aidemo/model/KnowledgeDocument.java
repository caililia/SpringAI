package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * RAG 知识库 - 文档模型
 */
public class KnowledgeDocument {
    private String id;
    private String title;
    private String content;
    private String fileType;    // pdf, txt, md, docx
    private Long fileSize;
    private String fileName;
    private Integer chunkCount; // 分块数量
    private LocalDateTime uploadedAt;
    private String status;      // processing, completed, failed

    public KnowledgeDocument() {
        this.uploadedAt = LocalDateTime.now();
    }

    public KnowledgeDocument(String title, String content, String fileType) {
        this();
        this.title = title;
        this.content = content;
        this.fileType = fileType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getChunkCount() { return chunkCount; }
    public void setChunkCount(Integer chunkCount) { this.chunkCount = chunkCount; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
