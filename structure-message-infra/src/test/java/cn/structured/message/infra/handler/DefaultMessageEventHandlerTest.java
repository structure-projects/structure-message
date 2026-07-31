package cn.structured.message.infra.handler;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.infra.handler.DefaultMessageEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * 默认消息事件处理器单元测试
 * <p>
 * 测试消息发送成功和失败事件的处理逻辑
 * </p>
 */
class DefaultMessageEventHandlerTest {

    private DefaultMessageEventHandler eventHandler;
    private MessageContext context;

    @BeforeEach
    void setUp() {
        eventHandler = new DefaultMessageEventHandler();
        context = new MessageContext();
        context.setChannelCode("SMS_ALIYUN");
        context.setReceiver("13800138000");
        context.setMessageId(1L);
    }

    @Test
    void onMessageSent_shouldHandleSuccessEvent() {
        MessageResult result = MessageResult.success(1L, "SMS_ALIYUN", "13800138000", "send-ok");

        assertDoesNotThrow(() -> eventHandler.onMessageSent(context, result));
    }

    @Test
    void onMessageFailed_shouldHandleFailedEvent() {
        String errorCode = "SEND_ERROR";
        String errorMsg = "网络超时";

        assertDoesNotThrow(() -> eventHandler.onMessageFailed(context, errorCode, errorMsg));
    }

    @Test
    void onMessageSent_withNullContext_shouldNotThrowException() {
        MessageResult result = MessageResult.success(1L, "SMS_ALIYUN", "13800138000", "send-ok");

        assertDoesNotThrow(() -> eventHandler.onMessageSent(null, result));
    }

    @Test
    void onMessageFailed_withNullContext_shouldNotThrowException() {
        assertDoesNotThrow(() -> eventHandler.onMessageFailed(null, "ERROR", "error"));
    }

    @Test
    void onMessageSent_withNullResult_shouldNotThrowException() {
        assertDoesNotThrow(() -> eventHandler.onMessageSent(context, null));
    }

    @Test
    void onMessageFailed_withNullErrorCode_shouldNotThrowException() {
        assertDoesNotThrow(() -> eventHandler.onMessageFailed(context, null, "error"));
    }

    @Test
    void onMessageFailed_withNullErrorMsg_shouldNotThrowException() {
        assertDoesNotThrow(() -> eventHandler.onMessageFailed(context, "ERROR", null));
    }
}