package org.example.aidemo.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * AI 配置类
 *
 * <p>1.0.0.4 版本说明：
 * - DashScopeApi、DashScopeChatModel、DashScopeChatOptions 等由 Spring Boot 自动配置创建
 * - 本类只创建自动配置未提供的 ChatClient Bean
 * - 语音合成：直接注入自动配置的 DashScopeAudioSpeechModel
 * </p>
 */
@Configuration
public class AiConfig {

    /**
     * DashScope 聊天客户端（使用自动配置的 DashScopeChatModel）
     */
    @Bean
    public ChatClient dashscopeChatClient(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel).build();
    }

    /**
     * Ollama 聊天客户端（本地模型）
     */
    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel).build();
    }

    /**
     * 默认聊天客户端（优先使用 DashScope）
     */
    @Bean
    @Primary
    public ChatClient defaultChatClient(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel).build();
    }

    /**
     * 语音合成服务（1.0.0.4）
     * 直接暴露自动配置的 DashScopeAudioSpeechModel 为 SpeechSynthesisModel
     */
    @Bean("dashScopeSpeechClient")
    public SpeechSynthesisModel dashScopeSpeechClient(DashScopeAudioSpeechModel speechModel) {
        return speechModel;
    }
}
