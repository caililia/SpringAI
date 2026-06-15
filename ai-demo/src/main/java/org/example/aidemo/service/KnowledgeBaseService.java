package org.example.aidemo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业级 RAG 知识库服务
 * 实现文档管理、知识搜索、智能问答功能
 */
@Service
public class KnowledgeBaseService {

    // 模拟知识库存储（实际项目中使用 MySQL + Milvus）
    private final List<String> knowledgeBase = new ArrayList<>();

    public KnowledgeBaseService() {
        // 初始化一些示例知识
        knowledgeBase.add("Spring AI 是 Spring 官方提供的 AI 开发框架，支持多种大语言模型。");
        knowledgeBase.add("Milvus 是一个开源的向量数据库，专门用于 AI 应用中的向量检索。");
        knowledgeBase.add("RAG（检索增强生成）是一种结合检索和生成的 AI 技术。");
        knowledgeBase.add("DashScope 是阿里云提供的大模型服务平台。");
        knowledgeBase.add("Ollama 是一个本地大模型运行工具，支持多种开源模型。");
    }

    /**
     * 添加知识文档
     */
    public void addDocument(String content) {
        knowledgeBase.add(content);
    }

    /**
     * 知识搜索
     */
    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        for (String doc : knowledgeBase) {
            if (doc.contains(query) || isSimilar(query, doc)) {
                results.add(doc);
            }
        }
        return results;
    }

    /**
     * 智能问答
     */
    public String ask(String question) {
        if (knowledgeBase.isEmpty()) {
            return "知识库为空，请先添加知识文档。";
        }

        // 简单关键词匹配检索
        List<String> relevantDocs = search(question);

        if (relevantDocs.isEmpty()) {
            return "抱歉，知识库中没有找到相关信息。";
        }

        // 构建回答（实际项目中使用 LLM 生成）
        StringBuilder answer = new StringBuilder();
        answer.append("根据知识库，找到以下相关信息：\n\n");
        for (String doc : relevantDocs) {
            answer.append("- ").append(doc).append("\n");
        }
        return answer.toString();
    }

    /**
     * 获取知识库统计信息
     */
    public int getDocumentCount() {
        return knowledgeBase.size();
    }

    /**
     * 简单相似度判断（实际项目中使用向量相似度）
     */
    private boolean isSimilar(String query, String doc) {
        // 简单实现：检查是否有共同关键词
        String[] queryWords = query.split("\\s+");
        for (String word : queryWords) {
            if (doc.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
