package cn.structured.message.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通道配置领域实体单元测试
 * <p>
 * 测试通道配置实体的业务方法：enable()、disable()、setAsDefault()、unsetAsDefault()
 * </p>
 */
class ChannelConfigTest {

    private ChannelConfig channelConfig;

    @BeforeEach
    void setUp() {
        channelConfig = ChannelConfig.create(100L, 1L, "默认配置", "{\"accessKey\":\"test\"}");
        channelConfig.setId(1L);
        channelConfig.enable();
    }

    @Test
    void enable_shouldSetStatusTo1() {
        channelConfig.disable();
        channelConfig.enable();
        assertEquals(1, channelConfig.getStatus());
    }

    @Test
    void enable_whenAlreadyEnabled_shouldKeepStatus1() {
        channelConfig.enable();
        assertEquals(1, channelConfig.getStatus());
    }

    @Test
    void disable_shouldSetStatusTo0() {
        channelConfig.disable();
        assertEquals(0, channelConfig.getStatus());
    }

    @Test
    void disable_whenAlreadyDisabled_shouldKeepStatus0() {
        channelConfig.disable();
        channelConfig.disable();
        assertEquals(0, channelConfig.getStatus());
    }

    @Test
    void setAsDefault_shouldSetIsDefaultTo1() {
        channelConfig.setAsDefault();
        assertEquals(1, channelConfig.getIsDefault());
    }

    @Test
    void setAsDefault_whenAlreadyDefault_shouldKeepIsDefault1() {
        channelConfig.setAsDefault();
        channelConfig.setAsDefault();
        assertEquals(1, channelConfig.getIsDefault());
    }

    @Test
    void unsetAsDefault_shouldSetIsDefaultTo0() {
        channelConfig.setAsDefault();
        channelConfig.unsetAsDefault();
        assertEquals(0, channelConfig.getIsDefault());
    }

    @Test
    void unsetAsDefault_whenAlreadyNotDefault_shouldKeepIsDefault0() {
        channelConfig.unsetAsDefault();
        assertEquals(0, channelConfig.getIsDefault());
    }

    @Test
    void create_shouldCreateValidEntity() {
        assertNotNull(channelConfig.getId());
        assertEquals(100L, channelConfig.getOrgId());
        assertEquals(1L, channelConfig.getChannelId());
        assertEquals("默认配置", channelConfig.getConfigName());
        assertEquals("{\"accessKey\":\"test\"}", channelConfig.getConfigValue());
        assertEquals(1, channelConfig.getStatus());
        assertEquals(0, channelConfig.getIsDefault());
    }

    @Test
    void updateConfigValue_shouldUpdateValue() {
        channelConfig.updateConfigValue("{\"key\":\"value\"}");
        
        assertEquals("{\"key\":\"value\"}", channelConfig.getConfigValue());
    }
}