package cn.structured.message.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InternalMessage 实体单元测试")
class InternalMessageTest {

    @Test
    @DisplayName("create: 创建消息时应初始化 state=0, deleted=false")
    void createShouldInitializeDefaults() {
        InternalMessage msg = InternalMessage.create(0, "sender-1", "receiver-1",
                "测试主题", "测试内容", "sms", 1000L);

        assertEquals("测试主题", msg.getSubject());
        assertEquals("测试内容", msg.getContent());
        assertEquals("sender-1", msg.getSender());
        assertEquals("receiver-1", msg.getReceiver());
        assertEquals("sms", msg.getChannel());
        assertEquals(0, msg.getType());
        assertEquals(Long.valueOf(1000L), msg.getOrgId());
        assertEquals(0, msg.getState()); // 默认未读
        assertEquals(false, msg.getDeleted()); // 默认未删除
    }

    @Test
    @DisplayName("markRead: 将 state 设为 1")
    void markReadShouldSetStateToOne() {
        InternalMessage msg = new InternalMessage();
        msg.markRead();
        assertEquals(1, msg.getState());
    }

    @Test
    @DisplayName("markUnread: 将 state 设为 0")
    void markUnreadShouldSetStateToZero() {
        InternalMessage msg = new InternalMessage();
        msg.markRead(); // state=1
        msg.markUnread();
        assertEquals(0, msg.getState());
    }

    @Test
    @DisplayName("setId: 正常设置")
    void setIdShouldWork() {
        InternalMessage msg = new InternalMessage();
        msg.setId(100L);
        assertEquals(100L, msg.getId());
    }

    @Test
    @DisplayName("type 区分通知(0)和公告(1)和私信(2)")
    void typeShouldDistinguishCategories() {
        InternalMessage notice = InternalMessage.create(0, "s", "r", "t", "c", "ch", 1L);
        assertEquals(0, notice.getType());

        InternalMessage announce = InternalMessage.create(1, "s", "r", "t", "c", "ch", 1L);
        assertEquals(1, announce.getType());
    }
}
