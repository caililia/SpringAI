package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * 文生图请求模型
 */
public record ImageRequest(
    String prompt,
    String size,      // 1024x1024, 512x512等
    String style,     // 风格描述
    Integer n         // 生成数量
) {
    public ImageRequest(String prompt) {
        this(prompt, "1024x1024", null, 1);
    }
}

/**
 * 文生图响应模型
 */
    class ImageResponse {
    private boolean success;
    private String url;           // 图片URL
    private String base64Image;   // Base64编码的图片
    private String revisedPrompt; // 实际使用的提示词
    private String error;
    private LocalDateTime createdAt;

    public ImageResponse() {
        this.createdAt = LocalDateTime.now();
    }

    public ImageResponse(boolean success, String url) {
        this();
        this.success = success;
        this.url = url;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
    public String getRevisedPrompt() { return revisedPrompt; }
    public void setRevisedPrompt(String revisedPrompt) { this.revisedPrompt = revisedPrompt; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
