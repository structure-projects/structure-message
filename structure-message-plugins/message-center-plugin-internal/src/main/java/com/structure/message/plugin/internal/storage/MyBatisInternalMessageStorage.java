package com.structure.message.plugin.internal.storage;

import cn.structured.message.common.model.MessageAccessory;
import com.structure.message.plugin.internal.InternalMessageDTO;
import com.structure.message.plugin.internal.mapper.InternalMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MyBatisInternalMessageStorage implements InternalMessageStorage {

    private final InternalMessageMapper internalMessageMapper;

    @Override
    public Long saveMessage(InternalMessageDTO message) {
        internalMessageMapper.insert(message);
        log.info("站内消息已保存到数据库，消息ID：{}", message.getId());
        return message.getId();
    }

    @Override
    public void updateMessage(InternalMessageDTO message) {
        internalMessageMapper.update(message);
    }

    @Override
    public void deleteMessage(Long messageId) {
        internalMessageMapper.delete(messageId);
        log.info("站内消息已从数据库删除，消息ID：{}", messageId);
    }

    @Override
    public InternalMessageDTO getMessageById(Long messageId) {
        return internalMessageMapper.selectById(messageId);
    }

    @Override
    public List<InternalMessageDTO> getUserMessages(String userId, Long orgId, Boolean isRead, Integer limit) {
        return internalMessageMapper.selectByUser(userId, orgId, isRead, limit);
    }

    @Override
    public List<InternalMessageDTO> getUserMessagesWithAccessories(String userId, Long orgId, Boolean isRead, Integer limit) {
        List<InternalMessageDTO> messages = internalMessageMapper.selectByUser(userId, orgId, isRead, limit);
        for (InternalMessageDTO message : messages) {
            List<MessageAccessory> accessories = internalMessageMapper.selectAccessoriesByMessageId(message.getId());
            if (accessories != null && !accessories.isEmpty()) {
                message.setAccessories(accessories);
            }
        }
        return messages;
    }

    @Override
    public long getUnreadMessageCount(String userId, Long orgId) {
        return internalMessageMapper.countUnread(userId, orgId);
    }

    @Override
    public void markAsRead(Long messageId) {
        InternalMessageDTO message = internalMessageMapper.selectById(messageId);
        if (message != null && !Boolean.TRUE.equals(message.getIsRead())) {
            message.setIsRead(true);
            message.setReadTime(LocalDateTime.now());
            internalMessageMapper.update(message);
            log.info("消息已标记为已读，消息ID：{}", messageId);
        }
    }

    @Override
    public void markAsReadBatch(List<Long> messageIds) {
        internalMessageMapper.batchMarkAsRead(messageIds);
        log.info("批量标记消息为已读，消息数量：{}", messageIds.size());
    }
}
