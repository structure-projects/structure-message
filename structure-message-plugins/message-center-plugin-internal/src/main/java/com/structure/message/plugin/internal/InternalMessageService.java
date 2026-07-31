package com.structure.message.plugin.internal;

import com.structure.message.plugin.internal.storage.InternalMessageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalMessageService {

    private final InternalMessageStorage storage;

    public Long saveMessage(InternalMessageDTO message) {
        if (message.getType() == null) {
            message.setType(1);
        }
        if (message.getChannel() == null) {
            message.setChannel("1");
        }
        if (message.getState() == null) {
            message.setState(1);
        }
        if (message.getIsRead() == null) {
            message.setIsRead(false);
        }
        if (message.getDeleted() == null) {
            message.setDeleted(false);
        }
        if (message.getPriority() == null) {
            message.setPriority(5);
        }
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        return storage.saveMessage(message);
    }

    public long getUnreadMessageCount(String userId, Long orgId) {
        return storage.getUnreadMessageCount(userId, orgId);
    }

    public void markAsRead(Long messageId) {
        storage.markAsRead(messageId);
    }

    public void markAsReadBatch(List<Long> messageIds) {
        storage.markAsReadBatch(messageIds);
    }

    public List<InternalMessageDTO> getUserMessages(String userId, Long orgId, Boolean isRead, Integer limit) {
        return storage.getUserMessagesWithAccessories(userId, orgId, isRead, limit);
    }

    public void deleteMessage(Long messageId) {
        storage.deleteMessage(messageId);
    }
}
