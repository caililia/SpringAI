package org.example.aidemo.service;

import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisMessage;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 语音合成服务
 *
 * <p>1.0.0.4 版本说明：
 * 使用 SpeechSynthesisModel 接口，通过 SpeechSynthesisPrompt 调用
 * SpeechSynthesisPrompt 没有 builder，使用构造函数：
 * new SpeechSynthesisPrompt(List<SpeechSynthesisMessage>, options)
 * </p>
 */
@Service
public class SpeechService {

    private final SpeechSynthesisModel speechSynthesisModel;

    public SpeechService(@Qualifier("dashScopeSpeechClient") SpeechSynthesisModel speechSynthesisModel) {
        this.speechSynthesisModel = speechSynthesisModel;
    }

    /**
     * 文本转语音
     */
    public byte[] textToSpeech(String text) {
        try {
            SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(
                    List.of(new SpeechSynthesisMessage(text))
            );

            SpeechSynthesisResponse response = speechSynthesisModel.call(prompt);
            ByteBuffer audioBuffer = response.getResult().getOutput().getAudio();

            byte[] audioBytes = new byte[audioBuffer.remaining()];
            audioBuffer.get(audioBytes);

            return audioBytes;
        } catch (Exception e) {
            throw new RuntimeException("语音合成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 文本转语音（自定义参数）
     */
    public byte[] textToSpeech(String text, String voice, Double speed) {
        try {
            DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                    .model("sambert-zhichu-v1")
                    .voice(voice != null ? voice : "alloy")
                    .speed((float) (speed != null ? speed : 1.0))
                    .build();

            SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(
                    List.of(new SpeechSynthesisMessage(text)),
                    options
            );

            SpeechSynthesisResponse response = speechSynthesisModel.call(prompt);
            ByteBuffer audioBuffer = response.getResult().getOutput().getAudio();

            byte[] audioBytes = new byte[audioBuffer.remaining()];
            audioBuffer.get(audioBytes);

            return audioBytes;
        } catch (Exception e) {
            throw new RuntimeException("语音合成失败: " + e.getMessage(), e);
        }
    }
}
