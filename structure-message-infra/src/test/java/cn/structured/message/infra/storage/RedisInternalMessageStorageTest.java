package cn.structured.message.infra.storage;

import cn.structured.message.domain.entity.InternalMessage;
import cn.structured.message.infra.storage.RedisInternalMessageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Redis内部消息存储单元测试
 * <p>
 * 使用Mockito模拟RedisTemplate，测试消息的保存、获取和删除操作
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class RedisInternalMessageStorageTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisInternalMessageStorage storage;

    @BeforeEach
    void setUp() {
        storage = new RedisInternalMessageStorage(redisTemplate);
    }

    @Test
    void save_shouldStoreMessageToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        InternalMessage message = InternalMessage.create(0, "user-001", "user-002", "测试消息", 
                "测试内容", "INTERNAL", 100L);
        message.setId(1L);

        assertDoesNotThrow(() -> storage.save(message));

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq("message:internal:1"), any(String.class), eq(24L), any());
    }

    @Test
    void get_withExistingMessage_shouldReturnMessage() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String json = "{\"id\":1,\"type\":0,\"sender\":\"user-001\",\"receiver\":\"user-002\",\"subject\":\"测试消息\",\"content\":\"测试内容\",\"channel\":\"INTERNAL\",\"state\":0,\"orgId\":100}";
        when(valueOperations.get("message:internal:1")).thenReturn(json);

        InternalMessage result = storage.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user-001", result.getSender());
        assertEquals("user-002", result.getReceiver());
        assertEquals("测试消息", result.getSubject());
        assertEquals("测试内容", result.getContent());
        assertEquals("INTERNAL", result.getChannel());
        assertEquals(0, result.getState());
        assertEquals(100L, result.getOrgId());
    }

    @Test
    void get_withNonExistingMessage_shouldReturnNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("message:internal:999")).thenReturn(null);

        InternalMessage result = storage.get(999L);

        assertNull(result);
    }

    @Test
    void delete_shouldRemoveMessageFromRedis() {
        assertDoesNotThrow(() -> storage.delete(1L));

        verify(redisTemplate).delete("message:internal:1");
    }

    @Test
    void findByReceiver_shouldReturnEmptyList() {
        var result = storage.findByReceiver("user-001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_withNullMessage_shouldNotThrowException() {
        assertDoesNotThrow(() -> storage.save(null));
    }

    @Test
    void get_withNullId_shouldReturnNull() {
        assertNull(storage.get(null));
    }

    @Test
    void delete_withNullId_shouldNotThrowException() {
        assertDoesNotThrow(() -> storage.delete(null));
    }
}