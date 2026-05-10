package com.structure.message.plugin.internal.storage;

import com.structure.message.plugin.internal.InternalMessageDTO;

import java.util.List;

public interface InternalMessageStorage {

    Long saveMessage(InternalMessageDTO message);

    void updateMessage(InternalMessageDTO message);

    void deleteMessage(Long messageId);

    InternalMessageDTO getMessageById(Long messageId);

    List<InternalMessageDTO> getUserMessages(String userId, Long orgId, Boolean isRead, Integer limit);

    long getUnreadMessageCount(String userId, Long orgId);

    void markAsRead(Long messageId);

    void markAsReadBatch(List<Long> messageIds);
}