package cn.structure.message.starter.storage;

import com.alibaba.fastjson.JSON;
import com.structure.message.plugin.internal.InternalMessageDTO;
import com.structure.message.plugin.internal.storage.InternalMessageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RedisInternalMessageStorage implements InternalMessageStorage {

    private static final String KEY_PREFIX = "internal:message:";
    private static final String USER_MESSAGE_KEY_PREFIX = "internal:user:messages:";
    private static final String USER_UNREAD_COUNT_KEY_PREFIX = "internal:user:unread:";
    private static final String MESSAGE_ID_SET_KEY = "internal:message:ids";
    private static final long DEFAULT_TTL_DAYS = 30;

    @Value("${message.internal.storage.ttl-days:30}")
    private long ttlDays = DEFAULT_TTL_DAYS;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long saveMessage(InternalMessageDTO message) {
        if (message.getId() == null) {
            Long messageId = redisTemplate.opsForValue().increment(MESSAGE_ID_SET_KEY + ":current");
            message.setId(messageId);
        }
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());

        String messageKey = KEY_PREFIX + message.getId();
        redisTemplate.opsForValue().set(messageKey, toJson(message), ttlDays, TimeUnit.DAYS);

        String userMessageKey = USER_MESSAGE_KEY_PREFIX + message.getOrgId() + ":" + message.getReceiver();
        redisTemplate.opsForZSet().add(userMessageKey, message.getId().toString(), message.getCreateTime().toEpochSecond(java.time.ZoneOffset.UTC));

        redisTemplate.opsForHash().increment(USER_UNREAD_COUNT_KEY_PREFIX + message.getOrgId() + ":" + message.getReceiver(), "unread", 1);

        log.info("站内消息已保存到Redis，消息ID：{}，接收者：{}", message.getId(), message.getReceiver());
        return message.getId();
    }

    @Override
    public void updateMessage(InternalMessageDTO message) {
        String messageKey = KEY_PREFIX + message.getId();
        redisTemplate.opsForValue().set(messageKey, toJson(message), ttlDays, TimeUnit.DAYS);
        log.info("站内消息已在Redis更新，消息ID：{}", message.getId());
    }

    @Override
    public void deleteMessage(Long messageId) {
        InternalMessageDTO message = getMessageById(messageId);
        if (message != null) {
            String messageKey = KEY_PREFIX + messageId;
            redisTemplate.delete(messageKey);

            String userMessageKey = USER_MESSAGE_KEY_PREFIX + message.getOrgId() + ":" + message.getReceiver();
            redisTemplate.opsForZSet().remove(userMessageKey, messageId.toString());

            if (!Boolean.TRUE.equals(message.getIsRead())) {
                redisTemplate.opsForHash().increment(USER_UNREAD_COUNT_KEY_PREFIX + message.getOrgId() + ":" + message.getReceiver(), "unread", -1);
            }
        }
        log.info("站内消息已从Redis删除，消息ID：{}", messageId);
    }

    @Override
    public InternalMessageDTO getMessageById(Long messageId) {
        String messageKey = KEY_PREFIX + messageId;
        Object value = redisTemplate.opsForValue().get(messageKey);
        if (value != null) {
            return fromJson(value.toString(), InternalMessageDTO.class);
        }
        return null;
    }

    @Override
    public List<InternalMessageDTO> getUserMessages(String userId, Long orgId, Boolean isRead, Integer limit) {
        String userMessageKey = USER_MESSAGE_KEY_PREFIX + orgId + ":" + userId;
        Set<Object> messageIds = redisTemplate.opsForZSet().reverseRange(userMessageKey, 0, limit != null ? limit - 1 : -1);

        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<InternalMessageDTO> messages = new ArrayList<>();
        for (Object idObj : messageIds) {
            Long messageId = Long.parseLong(idObj.toString());
            InternalMessageDTO message = getMessageById(messageId);
            if (message != null) {
                if (isRead == null || isRead.equals(message.getIsRead())) {
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    @Override
    public long getUnreadMessageCount(String userId, Long orgId) {
        String unreadKey = USER_UNREAD_COUNT_KEY_PREFIX + orgId + ":" + userId;
        Object count = redisTemplate.opsForHash().get(unreadKey, "unread");
        if (count != null) {
            return Long.parseLong(count.toString());
        }
        return 0;
    }

    @Override
    public void markAsRead(Long messageId) {
        InternalMessageDTO message = getMessageById(messageId);
        if (message != null && !Boolean.TRUE.equals(message.getIsRead())) {
            message.setIsRead(true);
            message.setReadTime(LocalDateTime.now());
            updateMessage(message);

            String unreadKey = USER_UNREAD_COUNT_KEY_PREFIX + message.getOrgId() + ":" + message.getReceiver();
            redisTemplate.opsForHash().increment(unreadKey, "unread", -1);

            log.info("Redis中的消息已标记为已读，消息ID：{}", messageId);
        }
    }

    @Override
    public void markAsReadBatch(List<Long> messageIds) {
        for (Long messageId : messageIds) {
            markAsRead(messageId);
        }
        log.info("批量标记消息为已读，消息数量：{}", messageIds.size());
    }

    private String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}