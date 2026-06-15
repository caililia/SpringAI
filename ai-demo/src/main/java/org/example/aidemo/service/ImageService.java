package org.example.aidemo.service;

import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文生图服务
 *
 * <p>1.0.0.4 版本说明：
 * 使用 ImageModel 接口，通过 ImagePrompt 调用 DashScopeImageModel
 * ImageGeneration.getOutput() 返回 Image，Image.getUrl() 获取图片URL
 * </p>
 */
@Service
public class ImageService {

    private final ImageModel imageModel;

    public ImageService(@Qualifier("dashScopeImageModel") ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    /**
     * 根据文本描述生成图片
     * 使用 DashScope 的图片生成能力
     */
    public String generateImage(String prompt) {
        try {
            ImagePrompt imagePrompt = new ImagePrompt(prompt);
            ImageResponse response = imageModel.call(imagePrompt);

            List<ImageGeneration> generations = response.getResults();
            if (generations != null && !generations.isEmpty()) {
                // ImageGeneration.getOutput() 返回 Image 对象
                return generations.get(0).getOutput().getUrl();
            }

            return "图片生成失败：未返回结果";
        } catch (Exception e) {
            return "图片生成失败: " + e.getMessage();
        }
    }

    /**
     * 生成图片并优化提示词
     */
    public String generateImageWithOptimization(String userPrompt) {
        try {
            // 第一步：优化提示词（通过 ChatClient）
            // 这里简化处理，直接使用用户输入的提示词
            return generateImage(userPrompt);
        } catch (Exception e) {
            return "图片生成失败: " + e.getMessage();
        }
    }
}
