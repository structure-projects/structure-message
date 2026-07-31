package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.impl.MessageServiceImpl;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.plugin.MessageChannelPlugin;
import cn.structured.message.domain.handler.MessageEventHandler;
import cn.structured.message.domain.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private PluginManager pluginManager;

    @Mock
    private MessageEventHandler messageEventHandler;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MessageChannelPlugin mockPlugin;

    private MessageServiceImpl service;

    private MessageContext context;

    @BeforeEach
    void setUp() {
        service = new MessageServiceImpl(pluginManager, messageEventHandler, rabbitTemplate);
        
        context = new MessageContext();
        context.setChannelCode("SMS_ALIYUN");
        context.setReceiver("13800138000");
        context.setOrgId(100L);
        context.setConfigName("default");
        context.setMessageId(123L);
        context.setBusinessSource("test");
    }

    @Test
    void send_withValidContext_shouldPublishToQueue() {
        MessageResult result = service.send(context);

        assertTrue(result.isSuccess());
        verify(rabbitTemplate).convertAndSend("message.send", context);
    }

    @Test
    void send_withNullContext_shouldReturnFailure() {
        MessageResult result = service.send(null);

        assertFalse(result.isSuccess());
        assertEquals("CONTEXT_NULL", result.getErrorCode());
    }

    @Test
    void sendSync_withValidPlugin_shouldSendSuccessfully() {
        when(pluginManager.getPlugin("SMS_ALIYUN", 100L, "default")).thenReturn(mockPlugin);
        when(mockPlugin.send(any(MessageContext.class))).thenReturn(MessageResult.success(123L, "SMS_ALIYUN", "13800138000", "send-ok"));

        MessageResult result = service.sendSync(context);

        assertTrue(result.isSuccess());
        verify(messageEventHandler).onMessageSent(any(MessageContext.class), any(MessageResult.class));
    }

    @Test
    void sendSync_withPluginNotFound_shouldReturnFailResult() {
        when(pluginManager.getPlugin("SMS_ALIYUN", 100L, "default")).thenReturn(null);

        MessageResult result = service.sendSync(context);

        assertFalse(result.isSuccess());
        assertEquals("PLUGIN_NOT_FOUND", result.getErrorCode());
        verify(messageEventHandler).onMessageFailed(any(MessageContext.class), eq("PLUGIN_NOT_FOUND"), anyString());
    }

    @Test
    void sendSync_withPluginReturnFail_shouldHandleFailed() {
        when(pluginManager.getPlugin("SMS_ALIYUN", 100L, "default")).thenReturn(mockPlugin);
        when(mockPlugin.send(any(MessageContext.class))).thenReturn(MessageResult.failure("SMS_ALIYUN", "13800138000", "SEND_ERROR", "网络超时"));

        MessageResult result = service.sendSync(context);

        assertFalse(result.isSuccess());
        assertEquals("SEND_ERROR", result.getErrorCode());
        verify(messageEventHandler).onMessageFailed(any(MessageContext.class), eq("SEND_ERROR"), eq("网络超时"));
    }

    @Test
    void sendSync_withPluginThrowException_shouldCatchAndReturnFail() {
        when(pluginManager.getPlugin("SMS_ALIYUN", 100L, "default")).thenReturn(mockPlugin);
        when(mockPlugin.send(any(MessageContext.class))).thenThrow(new RuntimeException("插件内部错误"));

        MessageResult result = service.sendSync(context);

        assertFalse(result.isSuccess());
        assertEquals("SEND_ERROR", result.getErrorCode());
        verify(messageEventHandler).onMessageFailed(any(MessageContext.class), eq("SEND_ERROR"), eq("插件内部错误"));
    }

    @Test
    void sendSync_withNullContext_shouldNotThrowException() {
        assertDoesNotThrow(() -> service.sendSync(null));
    }

    @Test
    void sendSync_withEmptyChannelCode_shouldReturnFailResult() {
        context.setChannelCode("");
        when(pluginManager.getPlugin("", 100L, "default")).thenReturn(null);

        MessageResult result = service.sendSync(context);

        assertFalse(result.isSuccess());
        assertEquals("PLUGIN_NOT_FOUND", result.getErrorCode());
    }

    @Test
    void sendSync_withNullChannelCode_shouldReturnFailResult() {
        context.setChannelCode(null);
        when(pluginManager.getPlugin(null, 100L, "default")).thenReturn(null);

        MessageResult result = service.sendSync(context);

        assertFalse(result.isSuccess());
        assertEquals("PLUGIN_NOT_FOUND", result.getErrorCode());
    }
}