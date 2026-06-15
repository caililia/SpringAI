package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * 语音合成请求模型
 */
public record SpeechRequest(
    String text,           // 要合成的文本
    String voice,          // 音色：alloy, echo, fable, onyx, nova, shimmer
    String responseFormat, // 输出格式：mp3, opus, aac, flac, wav, pcm
    Double speed           // 语速 0.25-4.0
) {
    public SpeechRequest(String text) {
        this(text, "alloy", "mp3", 1.0);
    }
}

/**
 * 语音合成响应模型
 */
class SpeechResponse {
    private boolean success;
    private String audioBase64;  // Base64编码的音频
    private String format;       // 音频格式
    private String error;
    private LocalDateTime createdAt;

    public SpeechResponse() {
        this.createdAt = LocalDateTime.now();
    }

    public SpeechResponse(boolean success, String audioBase64, String format) {
        this();
        this.success = success;
        this.audioBase64 = audioBase64;
        this.format = format;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getAudioBase64() { return audioBase64; }
    public void setAudioBase64(String audioBase64) { this.audioBase64 = audioBase64; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
