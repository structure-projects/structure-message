package cn.structured.message.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息通道领域实体单元测试
 * <p>
 * 测试消息通道实体的业务方法：enable()、disable()
 * </p>
 */
class MessageChannelTest {

    private MessageChannel messageChannel;

    @BeforeEach
    void setUp() {
        messageChannel = MessageChannel.create("SMS_ALIYUN", "阿里云短信", "SMS", 
                "com.structure.message.plugin.sms.AliyunSmsPlugin");
        messageChannel.setId(1L);
        messageChannel.enable();
    }

    @Test
    void enable_shouldSetStatusTo1() {
        messageChannel.disable();
        messageChannel.enable();
        assertEquals(MessageChannel.Status.ENABLED, messageChannel.getStatus());
    }

    @Test
    void enable_whenAlreadyEnabled_shouldKeepStatus1() {
        messageChannel.enable();
        assertEquals(MessageChannel.Status.ENABLED, messageChannel.getStatus());
    }

    @Test
    void disable_shouldSetStatusTo0() {
        messageChannel.disable();
        assertEquals(MessageChannel.Status.DISABLED, messageChannel.getStatus());
    }

    @Test
    void disable_whenAlreadyDisabled_shouldKeepStatus0() {
        messageChannel.disable();
        messageChannel.disable();
        assertEquals(MessageChannel.Status.DISABLED, messageChannel.getStatus());
    }

    @Test
    void create_shouldCreateValidEntity() {
        assertNotNull(messageChannel.getId());
        assertEquals("SMS_ALIYUN", messageChannel.getChannelCode());
        assertEquals("阿里云短信", messageChannel.getChannelName());
        assertEquals("SMS", messageChannel.getChannelType());
        assertEquals("com.structure.message.plugin.sms.AliyunSmsPlugin", messageChannel.getPluginClass());
        assertEquals(MessageChannel.Status.ENABLED, messageChannel.getStatus());
    }

    @Test
    void isEnabled_shouldReturnTrueWhenEnabled() {
        assertTrue(messageChannel.isEnabled());
    }

    @Test
    void isEnabled_shouldReturnFalseWhenDisabled() {
        messageChannel.disable();
        assertFalse(messageChannel.isEnabled());
    }
}