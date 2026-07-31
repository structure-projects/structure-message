package cn.structured.message.domain.service.impl;

import cn.structure.common.exception.CommonException;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.event.DomainEventPublisher;
import cn.structured.message.domain.event.MessageSentEvent;
import cn.structured.message.domain.repository.MessageRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageDomainService 单元测试")
class MessageDomainServiceTest {

    @Mock
    private MessageRecordRepository messageRecordRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private MessageDomainService messageDomainService;

    // ========== createMessageRecord ==========

    @Test
    @DisplayName("createMessageRecord: 正常创建消息记录")
    void createMessageRecordShouldSucceed() {
        when(messageRecordRepository.save(any(MessageRecord.class)))
                .thenAnswer(inv -> {
                    MessageRecord r = inv.getArgument(0);
                    r.setId(1L);
                    return r;
                });

        MessageRecord record = messageDomainService.createMessageRecord(
                1000L, "biz-1", "sms", "13800138000",
                "验证码", "{}", "登录", "auth");

        assertNotNull(record);
        assertEquals(1L, record.getId());
        assertEquals(1000L, record.getOrgId());
        assertEquals("sms", record.getChannelCode());
        verify(messageRecordRepository).save(any(MessageRecord.class));
    }

    @Test
    @DisplayName("createMessageRecord: channelCode 为空时抛出异常")
    void createShouldThrowWhenChannelCodeEmpty() {
        CommonException ex = assertThrows(CommonException.class, () ->
                messageDomainService.createMessageRecord(1L, "biz", "", "r", "c", null, "s", "src"));
        assertEquals("CHANNEL_CODE_EMPTY", ex.getCode());
    }

    @Test
    @DisplayName("createMessageRecord: receiver 为空时抛出异常")
    void createShouldThrowWhenReceiverEmpty() {
        CommonException ex = assertThrows(CommonException.class, () ->
                messageDomainService.createMessageRecord(1L, "biz", "sms", "", "c", null, "s", "src"));
        assertEquals("RECEIVER_EMPTY", ex.getCode());
    }

    @Test
    @DisplayName("createMessageRecord: content 为空时抛出异常")
    void createShouldThrowWhenContentEmpty() {
        CommonException ex = assertThrows(CommonException.class, () ->
                messageDomainService.createMessageRecord(1L, "biz", "sms", "r", "", null, "s", "src"));
        assertEquals("CONTENT_EMPTY", ex.getCode());
    }

    // ========== validateChannelConfig ==========

    @Test
    @DisplayName("validateChannelConfig: config 为 null 时抛出 NPE")
    void validateShouldThrowNPEWhenConfigNull() {
        assertThrows(NullPointerException.class,
                () -> messageDomainService.validateChannelConfig(null));
    }

    @Test
    @DisplayName("validateChannelConfig: status=0(禁用)时抛出异常")
    void validateShouldThrowWhenStatusDisabled() {
        ChannelConfig config = ChannelConfig.create(1L, 1L, "test", "val");
        // create() 默认 status=0 (禁用)
        config.setId(1L);

        CommonException ex = assertThrows(CommonException.class,
                () -> messageDomainService.validateChannelConfig(config));
        assertEquals("CHANNEL_CONFIG_DISABLED", ex.getCode());
    }

    @Test
    @DisplayName("validateChannelConfig: configValue 为空时抛出异常")
    void validateShouldThrowWhenConfigValueEmpty() {
        ChannelConfig config = ChannelConfig.create(1L, 1L, "test", "");
        config.enable(); // status=1
        config.setId(1L);

        CommonException ex = assertThrows(CommonException.class,
                () -> messageDomainService.validateChannelConfig(config));
        assertEquals("CHANNEL_CONFIG_VALUE_EMPTY", ex.getCode());
    }

    @Test
    @DisplayName("validateChannelConfig: 正常配置验证通过")
    void validateShouldPassForValidConfig() {
        ChannelConfig config = ChannelConfig.create(1L, 1L, "test", "valid-config");
        config.enable();
        config.setId(1L);

        assertDoesNotThrow(() -> messageDomainService.validateChannelConfig(config));
    }

    // ========== handleSendFailure ==========

    @Test
    @DisplayName("handleSendFailure: 记录不存在时抛出异常")
    void handleSendFailureShouldThrowWhenRecordNotFound() {
        when(messageRecordRepository.findById(99L)).thenReturn(null);

        CommonException ex = assertThrows(CommonException.class,
                () -> messageDomainService.handleSendFailure(99L, "timeout"));

        assertEquals("MESSAGE_RECORD_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("handleSendFailure: 正常标记失败并增加重试次数")
    void handleSendFailureShouldMarkFailedAndRetry() {
        MessageRecord record = MessageRecord.create(1L, "biz", "sms", "r", "c", null, "s", "src");
        record.markSending();
        when(messageRecordRepository.findById(1L)).thenReturn(record);

        messageDomainService.handleSendFailure(1L, "网络超时");

        assertEquals(3, record.getStatus());
        assertEquals("网络超时", record.getErrorMsg());
        assertEquals(1, record.getRetryTimes()); // 重试次数+1
        verify(messageRecordRepository).save(record);
    }

    // ========== markSendSuccess ==========

    @Test
    @DisplayName("markSendSuccess: 记录不存在时抛出异常")
    void markSendSuccessShouldThrowWhenRecordNotFound() {
        when(messageRecordRepository.findById(99L)).thenReturn(null);

        CommonException ex = assertThrows(CommonException.class,
                () -> messageDomainService.markSendSuccess(99L));

        assertEquals("MESSAGE_RECORD_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("markSendSuccess: 标记成功并发布领域事件")
    void markSendSuccessShouldMarkAndPublishEvent() {
        MessageRecord record = MessageRecord.create(1L, "biz", "email", "a@t.com", "c", null, "s", "src");
        record.markSending();
        when(messageRecordRepository.findById(1L)).thenReturn(record);

        messageDomainService.markSendSuccess(1L);

        assertEquals(2, record.getStatus());
        assertNotNull(record.getSendTime());
        verify(messageRecordRepository).save(record);
        verify(domainEventPublisher).publish(any(MessageSentEvent.class));
    }
}
