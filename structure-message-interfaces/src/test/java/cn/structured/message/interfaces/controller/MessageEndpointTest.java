package cn.structured.message.interfaces.controller;

import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 消息发送控制器单元测试
 * <p>
 * 使用MockMvc standalone模式测试消息发送的REST端点，不加载完整Spring上下文
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MessageEndpointTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageEndpoint messageEndpoint;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageEndpoint).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void sendMessage_shouldReturnResult() throws Exception {
        MessageContext context = new MessageContext();
        context.setChannelCode("SMS_ALIYUN");
        context.setReceiver("13800138000");
        context.setBusinessSource("test");

        MessageResult result = MessageResult.success(1L, "SMS_ALIYUN", "13800138000", "send-ok");
        when(messageService.send(any(MessageContext.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(context)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.channelCode").value("SMS_ALIYUN"));
    }

    @Test
    void sendMessageSync_shouldReturnResult() throws Exception {
        MessageContext context = new MessageContext();
        context.setChannelCode("SMS_ALIYUN");
        context.setReceiver("13800138000");
        context.setBusinessSource("test");

        MessageResult result = MessageResult.success(1L, "SMS_ALIYUN", "13800138000", "send-ok");
        when(messageService.sendSync(any(MessageContext.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/messages/send/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(context)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void sendMessage_withEmptyBody_shouldReturnOk() throws Exception {
        // 控制器未启用 @Valid 校验，空请求体会进入service层处理
        MessageResult failResult = MessageResult.failure(null, null, "INVALID_PARAM", "通道编码不能为空");
        when(messageService.send(any(MessageContext.class))).thenReturn(failResult);

        mockMvc.perform(post("/api/v1/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void sendMessage_withNullChannelCode_shouldReturnFail() throws Exception {
        MessageContext context = new MessageContext();
        context.setReceiver("13800138000");
        context.setBusinessSource("test");

        MessageResult failResult = MessageResult.failure(null, "13800138000", "PLUGIN_NOT_FOUND", "未找到通道插件: null");
        when(messageService.send(any(MessageContext.class))).thenReturn(failResult);

        mockMvc.perform(post("/api/v1/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(context)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("PLUGIN_NOT_FOUND"));
    }

    @Test
    void sendMessage_withEmptyReceiver_shouldReturnResult() throws Exception {
        MessageContext context = new MessageContext();
        context.setChannelCode("SMS_ALIYUN");
        context.setBusinessSource("test");

        MessageResult result = MessageResult.success(1L, "SMS_ALIYUN", null, "send-ok");
        when(messageService.send(any(MessageContext.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(context)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}