package cn.structured.message.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageAccessory 实体单元测试")
class MessageAccessoryTest {

    @Test
    @DisplayName("create: 创建附件时应初始化 state=1")
    void createShouldInitializeStateActive() {
        MessageAccessory accessory = MessageAccessory.create(10L, 1, "res-001", "测试附件");

        assertEquals(Long.valueOf(10L), accessory.getMessageId());
        assertEquals(1, accessory.getResourceType());
        assertEquals("res-001", accessory.getResourceId());
        assertEquals("测试附件", accessory.getResourceName());
        assertEquals(1, accessory.getState()); // 默认启用
    }

    @Test
    @DisplayName("activate: 将 state 设为 1")
    void activateShouldSetStateToOne() {
        MessageAccessory accessory = new MessageAccessory();
        accessory.activate();
        assertEquals(1, accessory.getState());
    }

    @Test
    @DisplayName("deactivate: 将 state 设为 0")
    void deactivateShouldSetStateToZero() {
        MessageAccessory accessory = new MessageAccessory();
        accessory.deactivate();
        assertEquals(0, accessory.getState());
    }

    @Test
    @DisplayName("activate / deactivate 状态切换")
    void activateAndDeactivateShouldToggleState() {
        MessageAccessory accessory = MessageAccessory.create(1L, 0, "r", "n");
        assertEquals(1, accessory.getState());

        accessory.deactivate();
        assertEquals(0, accessory.getState());

        accessory.activate();
        assertEquals(1, accessory.getState());
    }

    @Test
    @DisplayName("setId: 正常设置")
    void setIdShouldWork() {
        MessageAccessory accessory = new MessageAccessory();
        accessory.setId(200L);
        assertEquals(200L, accessory.getId());
    }
}
