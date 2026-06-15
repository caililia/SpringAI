package org.example.aidemo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Function Calling 工具服务
 * 实现天气查询、计算器等工具调用功能
 */
@Service
public class ToolService {

    private final ChatClient defaultClient;

    public ToolService(@Qualifier("defaultChatClient") ChatClient defaultClient) {
        this.defaultClient = defaultClient;
    }

    /**
     * 天气查询工具
     */
    public String queryWeather(String city) {
        // 模拟天气数据（实际项目中可接入真实天气API）
        return String.format(
            "【天气查询结果】\n" +
            "城市: %s\n" +
            "天气: 多云转晴\n" +
            "温度: 23°C\n" +
            "湿度: 65%%\n" +
            "风力: 东北风3级",
            city
        );
    }

    /**
     * 计算器工具 - 支持基本四则运算
     */
    public String calculate(String expression) {
        try {
            // 安全计算：只允许数字和运算符
            if (!expression.matches("[0-9+\\-*/().\\s]+")) {
                return "错误：表达式包含非法字符";
            }

            // 使用ScriptEngine计算（Java 21中已移除，使用替代方案）
            double result = evaluateExpression(expression);
            return String.format("计算结果: %s = %.4f", expression, result);
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }

    /**
     * 简单表达式计算器
     */
    private double evaluateExpression(String expr) {
        // 移除空格
        expr = expr.replaceAll("\\s+", "");

        // 处理加减法
        String[] parts = expr.split("(?=[-+])|(?<=[-+])");
        double result = 0;
        boolean isAddition = true;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (part.equals("+")) {
                isAddition = true;
                continue;
            }
            if (part.equals("-")) {
                isAddition = false;
                continue;
            }

            // 处理乘除
            double value = evaluateMulDiv(part);
            if (isAddition) {
                result += value;
            } else {
                result -= value;
            }
        }

        return result;
    }

    private double evaluateMulDiv(String expr) {
        String[] parts = expr.split("(?=[*/])|(?<=[*/])");
        double result = Double.parseDouble(parts[0]);

        for (int i = 1; i < parts.length; i += 2) {
            String op = parts[i];
            double val = Double.parseDouble(parts[i + 1]);
            if (op.equals("*")) {
                result *= val;
            } else if (op.equals("/")) {
                result /= val;
            }
        }

        return result;
    }

    /**
     * 使用 AI 进行智能工具调用
     * 让 AI 判断应该调用哪个工具
     */
    public String smartToolCall(String userInput) {
        String systemPrompt = """
            你是一个智能助手，可以根据用户输入判断需要调用哪个工具。
            可用工具：
            1. 天气查询 - 当用户询问天气时，返回格式：WEATHER:城市名
            2. 计算器 - 当用户需要进行数学计算时，返回格式：CALCULATE:数学表达式
            
            如果不需要调用工具，直接回答用户问题。
            只返回工具调用格式或直接回答，不要有其他内容。
            """;

        String response = defaultClient.prompt()
                .system(systemPrompt)
                .user(userInput)
                .call()
                .content();

        // 解析工具调用
        if (response.startsWith("WEATHER:")) {
            String city = response.substring(8).trim();
            return queryWeather(city);
        } else if (response.startsWith("CALCULATE:")) {
            String expr = response.substring(10).trim();
            return calculate(expr);
        }

        return response;
    }
}
