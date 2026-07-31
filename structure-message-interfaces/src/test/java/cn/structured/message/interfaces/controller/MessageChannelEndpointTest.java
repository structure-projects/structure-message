package cn.structured.message.interfaces.controller;

import cn.structured.message.application.service.MessageChannelService;
import cn.structured.message.domain.entity.MessageChannel;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 消息通道控制器单元测试
 * <p>
 * 使用MockMvc standalone模式测试控制器的REST端点，不加载完整Spring上下文
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MessageChannelEndpointTest {

    private MockMvc mockMvc;

    @Mock
    private MessageChannelService messageChannelService;

    @InjectMocks
    private MessageChannelEndpoint messageChannelEndpoint;

    private ObjectMapper objectMapper;

    private MessageChannel testChannel;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageChannelEndpoint).build();
        objectMapper = new ObjectMapper();
        testChannel = MessageChannel.create("SMS_ALIYUN", "阿里云短信", "SMS", 
                "com.structure.message.plugin.sms.AliyunSmsPlugin");
        testChannel.setId(1L);
        testChannel.enable();
    }

    @Test
    void createChannel_shouldReturnCreatedChannel() throws Exception {
        when(messageChannelService.create(any(MessageChannel.class))).thenReturn(testChannel);

        mockMvc.perform(post("/api/v1/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testChannel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelCode").value("SMS_ALIYUN"))
                .andExpect(jsonPath("$.channelName").value("阿里云短信"));
    }

    @Test
    void updateChannel_shouldReturnUpdatedChannel() throws Exception {
        when(messageChannelService.update(anyLong(), any(MessageChannel.class))).thenReturn(testChannel);

        mockMvc.perform(put("/api/v1/channels/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testChannel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelCode").value("SMS_ALIYUN"));
    }

    @Test
    void deleteChannel_shouldReturnOk() throws Exception {
        doNothing().when(messageChannelService).delete(anyLong());

        mockMvc.perform(delete("/api/v1/channels/{id}", 1L))
                .andExpect(status().isOk());

        verify(messageChannelService).delete(1L);
    }

    @Test
    void getChannelById_shouldReturnChannel() throws Exception {
        when(messageChannelService.findById(1L)).thenReturn(testChannel);

        mockMvc.perform(get("/api/v1/channels/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelCode").value("SMS_ALIYUN"));
    }

    @Test
    void getChannelByCode_shouldReturnChannel() throws Exception {
        when(messageChannelService.findByChannelCode("SMS_ALIYUN")).thenReturn(testChannel);

        mockMvc.perform(get("/api/v1/channels/code/{channelCode}", "SMS_ALIYUN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelCode").value("SMS_ALIYUN"));
    }

    @Test
    void getAllChannels_shouldReturnChannelList() throws Exception {
        List<MessageChannel> channels = Arrays.asList(testChannel);
        when(messageChannelService.findAll()).thenReturn(channels);

        mockMvc.perform(get("/api/v1/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].channelCode").value("SMS_ALIYUN"));
    }

    @Test
    void getChannelsByStatus_shouldReturnFilteredChannels() throws Exception {
        List<MessageChannel> channels = Arrays.asList(testChannel);
        when(messageChannelService.findByStatus(1)).thenReturn(channels);

        mockMvc.perform(get("/api/v1/channels/status/{status}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getChannelsByType_shouldReturnFilteredChannels() throws Exception {
        List<MessageChannel> channels = Arrays.asList(testChannel);
        when(messageChannelService.findByChannelType("SMS")).thenReturn(channels);

        mockMvc.perform(get("/api/v1/channels/type/{channelType}", "SMS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void enableChannel_shouldReturnOk() throws Exception {
        doNothing().when(messageChannelService).enable(anyLong());

        mockMvc.perform(post("/api/v1/channels/{id}/enable", 1L))
                .andExpect(status().isOk());

        verify(messageChannelService).enable(1L);
    }

    @Test
    void disableChannel_shouldReturnOk() throws Exception {
        doNothing().when(messageChannelService).disable(anyLong());

        mockMvc.perform(post("/api/v1/channels/{id}/disable", 1L))
                .andExpect(status().isOk());

        verify(messageChannelService).disable(1L);
    }
}