package cn.structured.message.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageRecord 实体单元测试")
class MessageRecordTest {

    @Test
    @DisplayName("create: 初始化 status=0(待发送), retryTimes=0")
    void createShouldInitializeDefaults() {
        MessageRecord record = MessageRecord.create(1000L, "biz-001", "sms_code",
                "13800138000", "验证码123456", "{\"code\":\"123456\"}",
                "登录验证码", "auth-service");

        assertEquals(Long.valueOf(1000L), record.getOrgId());
        assertEquals("biz-001", record.getBusinessId());
        assertEquals("sms_code", record.getChannelCode());
        assertEquals("13800138000", record.getReceiver());
        assertEquals("验证码123456", record.getContent());
        assertEquals("{\"code\":\"123456\"}", record.getParams());
        assertEquals("登录验证码", record.getSubject());
        assertEquals("auth-service", record.getBusinessSource());
        assertEquals(0, record.getStatus()); // 待发送
        assertEquals(0, record.getRetryTimes()); // 默认 0
    }

    @Test
    @DisplayName("markPending: status=0")
    void markPendingShouldSetStatusZero() {
        MessageRecord record = new MessageRecord();
        record.markPending();
        assertEquals(0, record.getStatus());
    }

    @Test
    @DisplayName("markSending: status=1")
    void markSendingShouldSetStatusOne() {
        MessageRecord record = new MessageRecord();
        record.markSending();
        assertEquals(1, record.getStatus());
    }

    @Test
    @DisplayName("markSuccess: status=2 并设置 sendTime")
    void markSuccessShouldSetStatusTwoAndSendTime() {
        MessageRecord record = new MessageRecord();
        record.markSuccess();

        assertEquals(2, record.getStatus());
        assertNotNull(record.getSendTime());
        assertTrue(record.getSendTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("markFailed: status=3 并记录 errorMsg")
    void markFailedShouldSetStatusThreeAndErrorMsg() {
        MessageRecord record = new MessageRecord();
        record.markFailed("发送超时");

        assertEquals(3, record.getStatus());
        assertEquals("发送超时", record.getErrorMsg());
    }

    @Test
    @DisplayName("incrementRetry: retryTimes=null 时先初始化为0再加1")
    void incrementRetryShouldHandleNullRetryTimes() {
        MessageRecord record = new MessageRecord();
        assertNull(record.getRetryTimes());

        record.incrementRetry();
        assertEquals(1, record.getRetryTimes());
    }

    @Test
    @DisplayName("incrementRetry: 正常累加重试次数")
    void incrementRetryShouldIncrement() {
        MessageRecord record = new MessageRecord();
        record.incrementRetry(); // 1
        record.incrementRetry(); // 2
        record.incrementRetry(); // 3

        assertEquals(3, record.getRetryTimes());
    }

    @Test
    @DisplayName("setId/setChannelId/setTemplateId: 正常设置")
    void settersShouldWork() {
        MessageRecord record = new MessageRecord();
        record.setId(100L);
        record.setChannelId(200L);
        record.setTemplateId(300L);

        assertEquals(100L, record.getId());
        assertEquals(200L, record.getChannelId());
        assertEquals(300L, record.getTemplateId());
    }

    @Test
    @DisplayName("完整状态流转: Pending → Sending → Success")
    void fullStateMachineShouldWork() {
        MessageRecord record = MessageRecord.create(
                1L, "biz-1", "email", "a@test.com", "hello", null, "subj", "src");

        assertEquals(0, record.getStatus()); // Pending

        record.markSending();
        assertEquals(1, record.getStatus()); // Sending

        record.markSuccess();
        assertEquals(2, record.getStatus()); // Success
        assertNotNull(record.getSendTime());
    }

    @Test
    @DisplayName("失败重试场景: Pending → Sending → Failed → incrementRetry")
    void failureRetryScenarioShouldWork() {
        MessageRecord record = new MessageRecord();
        record.markSending();
        record.markFailed("网络错误");
        assertEquals(3, record.getStatus());
        assertEquals("网络错误", record.getErrorMsg());

        record.incrementRetry();
        assertEquals(1, record.getRetryTimes());
    }
}
