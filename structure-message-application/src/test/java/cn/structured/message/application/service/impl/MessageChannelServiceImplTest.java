package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.impl.MessageChannelServiceImpl;
import cn.structured.message.common.exception.MessageException;
import cn.structured.message.domain.entity.MessageChannel;
import cn.structured.message.domain.repository.MessageChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageChannelServiceImplTest {

    @Mock
    private MessageChannelRepository messageChannelRepository;

    private MessageChannelServiceImpl service;

    private MessageChannel testChannel;

    @BeforeEach
    void setUp() {
        service = new MessageChannelServiceImpl(messageChannelRepository);
        testChannel = MessageChannel.create("SMS_ALIYUN", "阿里云短信", "SMS", 
                "com.structure.message.plugin.sms.AliyunSmsPlugin");
        testChannel.setId(1L);
        testChannel.enable();
    }

    @Test
    void create_withNewChannel_shouldSaveSuccessfully() {
        when(messageChannelRepository.existsByChannelCode(anyString())).thenReturn(false);
        when(messageChannelRepository.save(any(MessageChannel.class))).thenReturn(testChannel);

        MessageChannel result = service.create(testChannel);

        assertNotNull(result);
        assertEquals("SMS_ALIYUN", result.getChannelCode());
        assertTrue(result.isEnabled());
        verify(messageChannelRepository).existsByChannelCode("SMS_ALIYUN");
        verify(messageChannelRepository).save(any(MessageChannel.class));
    }

    @Test
    void create_withExistingChannelCode_shouldThrowException() {
        when(messageChannelRepository.existsByChannelCode("SMS_ALIYUN")).thenReturn(true);

        assertThrows(MessageException.class, () -> service.create(testChannel));
        verify(messageChannelRepository, never()).save(any(MessageChannel.class));
    }

    @Test
    void create_withNewChannel_shouldDefaultToDisabled() {
        MessageChannel newChannel = MessageChannel.create("SMS_TENCENT", "腾讯短信", "SMS", 
                "com.structure.message.plugin.sms.TencentSmsPlugin");
        
        when(messageChannelRepository.existsByChannelCode(anyString())).thenReturn(false);
        when(messageChannelRepository.save(any(MessageChannel.class))).thenAnswer(invocation -> {
            MessageChannel saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        MessageChannel result = service.create(newChannel);

        assertFalse(result.isEnabled());
    }

    @Test
    void update_withValidChannel_shouldUpdateSuccessfully() {
        MessageChannel updatedChannel = MessageChannel.create("SMS_ALIYUN", "阿里云短信-更新", "SMS", 
                "com.structure.message.plugin.sms.AliyunSmsPlugin");
        updatedChannel.disable();

        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);
        when(messageChannelRepository.existsByChannelCodeAndNeId("SMS_ALIYUN", 1L)).thenReturn(false);
        when(messageChannelRepository.save(any(MessageChannel.class))).thenReturn(testChannel);

        MessageChannel result = service.update(1L, updatedChannel);

        assertNotNull(result);
        verify(messageChannelRepository).findById(1L);
        verify(messageChannelRepository).save(any(MessageChannel.class));
    }

    @Test
    void update_withNonExistingChannel_shouldThrowException() {
        when(messageChannelRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.update(1L, testChannel));
    }

    @Test
    void update_withDuplicateChannelCode_shouldThrowException() {
        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);
        when(messageChannelRepository.existsByChannelCodeAndNeId("SMS_ALIYUN", 1L)).thenReturn(true);

        assertThrows(MessageException.class, () -> service.update(1L, testChannel));
    }

    @Test
    void delete_withExistingChannel_shouldDeleteSuccessfully() {
        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);
        doNothing().when(messageChannelRepository).removeById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(messageChannelRepository).removeById(1L);
    }

    @Test
    void delete_withNonExistingChannel_shouldThrowException() {
        when(messageChannelRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.delete(1L));
    }

    @Test
    void findById_withExistingChannel_shouldReturnChannel() {
        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);

        MessageChannel result = service.findById(1L);

        assertNotNull(result);
        assertEquals("SMS_ALIYUN", result.getChannelCode());
    }

    @Test
    void findById_withNonExistingChannel_shouldThrowException() {
        when(messageChannelRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.findById(1L));
    }

    @Test
    void findByChannelCode_withExistingChannel_shouldReturnChannel() {
        when(messageChannelRepository.findByChannelCode("SMS_ALIYUN")).thenReturn(Optional.of(testChannel));

        MessageChannel result = service.findByChannelCode("SMS_ALIYUN");

        assertNotNull(result);
        assertEquals("SMS_ALIYUN", result.getChannelCode());
    }

    @Test
    void findByChannelCode_withNonExistingChannel_shouldThrowException() {
        when(messageChannelRepository.findByChannelCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(MessageException.class, () -> service.findByChannelCode("UNKNOWN"));
    }

    @Test
    void findAll_shouldReturnChannelList() {
        MessageChannel channel2 = MessageChannel.create("EMAIL_SMTP", "SMTP邮件", "EMAIL", 
                "com.structure.message.plugin.email.SmtpEmailPlugin");
        channel2.setId(2L);
        channel2.enable();
        
        when(messageChannelRepository.queryList(null)).thenReturn(Arrays.asList(testChannel, channel2));

        List<MessageChannel> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByStatus_shouldReturnFilteredChannels() {
        when(messageChannelRepository.findByStatus(1)).thenReturn(Arrays.asList(testChannel));

        List<MessageChannel> result = service.findByStatus(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByChannelType_shouldReturnFilteredChannels() {
        when(messageChannelRepository.findByChannelType("SMS")).thenReturn(Arrays.asList(testChannel));

        List<MessageChannel> result = service.findByChannelType("SMS");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void enable_shouldUpdateChannelStatus() {
        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);
        when(messageChannelRepository.save(any(MessageChannel.class))).thenReturn(testChannel);

        assertDoesNotThrow(() -> service.enable(1L));

        verify(messageChannelRepository).save(any(MessageChannel.class));
    }

    @Test
    void disable_shouldUpdateChannelStatus() {
        when(messageChannelRepository.findById(1L)).thenReturn(testChannel);
        when(messageChannelRepository.save(any(MessageChannel.class))).thenReturn(testChannel);

        assertDoesNotThrow(() -> service.disable(1L));

        verify(messageChannelRepository).save(any(MessageChannel.class));
    }
}