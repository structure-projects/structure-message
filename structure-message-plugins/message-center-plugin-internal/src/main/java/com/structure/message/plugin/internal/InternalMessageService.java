package com.structure.message.plugin.internal;

import com.structure.message.plugin.internal.storage.InternalMessageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalMessageService {

    @Qualifier("internalMessageStorage")
    private final InternalMessageStorage storage;

    public Long saveMessage(InternalMessageDTO message) {
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
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
        return storage.getUserMessages(userId, orgId, isRead, limit);
    }

    public void deleteMessage(Long messageId) {
        storage.deleteMessage(messageId);
    }
}
