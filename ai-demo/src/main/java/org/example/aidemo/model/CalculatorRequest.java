package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * 计算器请求模型
 */
public record CalculatorRequest(
    String expression  // 数学表达式，如 "2+3*4"
) {}

/**
 * 计算器响应模型
 */
class CalculatorResponse {
    private boolean success;
    private String expression;
    private Double result;
    private String error;
    private LocalDateTime createdAt;

    public CalculatorResponse() {
        this.createdAt = LocalDateTime.now();
    }

    public CalculatorResponse(boolean success, String expression, Double result) {
        this();
        this.success = success;
        this.expression = expression;
        this.result = result;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
