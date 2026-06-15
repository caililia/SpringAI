package org.example.aidemo.service;

import org.example.aidemo.model.Conversation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多轮对话管理服务
 * 实现会话创建、历史消息管理、会话查询等功能
 */
@Service
public class ConversationService {

    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();

    /**
     * 创建新会话
     */
    public Conversation createConversation(String model) {
        String id = generateConversationId();
        Conversation conversation = new Conversation(id, model);
        conversations.put(id, conversation);
        return conversation;
    }

    /**
     * 获取会话
     */
    public Conversation getConversation(String id) {
        return conversations.get(id);
    }

    /**
     * 添加用户消息
     */
    public Conversation addUserMessage(String conversationId, String content) {
        Conversation conv = conversations.get(conversationId);
        if (conv != null) {
            conv.addUserMessage(content);
        }
        return conv;
    }

    /**
     * 添加助手回复
     */
    public Conversation addAssistantMessage(String conversationId, String content) {
        Conversation conv = conversations.get(conversationId);
        if (conv != null) {
            conv.addAssistantMessage(content);
        }
        return conv;
    }

    /**
     * 删除会话
     */
    public boolean deleteConversation(String id) {
        return conversations.remove(id) != null;
    }

    /**
     * 获取所有会话ID
     */
    public java.util.Set<String> getAllConversationIds() {
        return conversations.keySet();
    }

    /**
     * 获取所有会话
     */
    public java.util.List<Conversation> getAllConversations() {
        return new java.util.ArrayList<>(conversations.values());
    }

    private String generateConversationId() {
        return "conv_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}
