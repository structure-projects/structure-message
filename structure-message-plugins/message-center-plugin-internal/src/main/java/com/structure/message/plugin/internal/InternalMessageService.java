package com.structure.message.plugin.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站内消息服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InternalMessageService {

    // 模拟数据库存储
    private final Map<Long, InternalMessageDTO> messageStorage = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private volatile Long messageIdSequence = 1L;

    /**
     * 保存站内消息
     */
    public Long saveMessage(InternalMessageDTO message) {
        Long messageId = generateMessageId();
        message.setId(messageId);
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());

        messageStorage.put(messageId, message);

        log.info("站内消息已保存，消息ID：{}，接收者：{}", messageId, message.getReceiver());
        return messageId;
    }

    /**
     * 发送实时通知
     */
    public void sendRealtimeNotification(InternalMessageDTO message) {
        String userId = message.getReceiver();
        WebSocketSession session = userSessions.get(userId);

        if (session != null && session.isConnected()) {
            try {
                session.sendMessage(message);
                log.info("实时通知发送成功，用户ID：{}，消息ID：{}", userId, message.getId());
            } catch (Exception e) {
                log.error("实时通知发送失败，用户ID：{}，消息ID：{}", userId, message.getId(), e);
            }
        } else {
            log.info("用户不在线，跳过实时通知，用户ID：{}", userId);
        }
    }

    /**
     * 获取用户未读消息数量
     */
    public long getUnreadMessageCount(String userId, Long orgId) {
        return messageStorage.values().stream()
                .filter(msg -> userId.equals(msg.getReceiver()) && orgId.equals(msg.getOrgId()) && !msg.getIsRead())
                .count();
    }

    /**
     * 标记消息为已读
     */
    public void markAsRead(Long messageId) {
        InternalMessageDTO message = messageStorage.get(messageId);
        if (message != null && !message.getIsRead()) {
            message.setIsRead(true);
            message.setReadTime(LocalDateTime.now());
            log.info("消息已标记为已读，消息ID：{}", messageId);
        }
    }

    /**
     * 批量标记消息为已读
     */
    public void markAsReadBatch(List<Long> messageIds) {
        messageIds.forEach(this::markAsRead);
    }

    /**
     * 获取用户消息列表
     */
    public List<InternalMessageDTO> getUserMessages(String userId, Long orgId, Boolean isRead, Integer limit) {
        return messageStorage.values().stream()
                .filter(msg -> userId.equals(msg.getReceiver()) && orgId.equals(msg.getOrgId()))
                .filter(msg -> isRead == null || msg.getIsRead().equals(isRead))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(limit != null ? limit : 100)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 删除消息
     */
    public void deleteMessage(Long messageId) {
        InternalMessageDTO message = messageStorage.remove(messageId);
        if (message != null) {
            log.info("消息已删除，消息ID：{}", messageId);
        }
    }

    /**
     * 注册WebSocket会话
     */
    public void registerSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("WebSocket会话已注册，用户ID：{}", userId);
    }

    /**
     * 注销WebSocket会话
     */
    public void unregisterSession(String userId) {
        WebSocketSession session = userSessions.remove(userId);
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭WebSocket会话失败，用户ID：{}", userId, e);
            }
        }
        log.info("WebSocket会话已注销，用户ID：{}", userId);
    }

    /**
     * 生成消息ID
     */
    private synchronized Long generateMessageId() {
        return messageIdSequence++;
    }

    /**
     * WebSocket会话接口
     */
    public interface WebSocketSession {
        boolean isConnected();
        void sendMessage(InternalMessageDTO message) throws Exception;
        void close() throws Exception;
    }
}