package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.impl.ChannelConfigServiceImpl;
import cn.structured.message.common.exception.MessageException;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.repository.ChannelConfigRepository;
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
class ChannelConfigServiceImplTest {

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    private ChannelConfigServiceImpl service;

    private ChannelConfig testConfig;

    @BeforeEach
    void setUp() {
        service = new ChannelConfigServiceImpl(channelConfigRepository);
        testConfig = ChannelConfig.create(100L, 1L, "默认配置", "{\"accessKey\":\"test\"}");
        testConfig.setId(1L);
        testConfig.enable();
    }

    @Test
    void create_withNewConfig_shouldSaveSuccessfully() {
        when(channelConfigRepository.existsByOrgIdAndChannelIdAndConfigName(100L, 1L, "默认配置")).thenReturn(false);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenReturn(testConfig);

        ChannelConfig result = service.create(testConfig);

        assertNotNull(result);
        assertEquals("默认配置", result.getConfigName());
        verify(channelConfigRepository).save(any(ChannelConfig.class));
    }

    @Test
    void create_withExistingConfigName_shouldThrowException() {
        when(channelConfigRepository.existsByOrgIdAndChannelIdAndConfigName(100L, 1L, "默认配置")).thenReturn(true);

        assertThrows(MessageException.class, () -> service.create(testConfig));
        verify(channelConfigRepository, never()).save(any(ChannelConfig.class));
    }

    @Test
    void create_withNewConfig_shouldDefaultToDisabled() {
        ChannelConfig newConfig = ChannelConfig.create(100L, 1L, "新配置", "{\"key\":\"value\"}");
        
        when(channelConfigRepository.existsByOrgIdAndChannelIdAndConfigName(anyLong(), anyLong(), anyString())).thenReturn(false);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenAnswer(invocation -> {
            ChannelConfig saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ChannelConfig result = service.create(newConfig);

        assertEquals(0, result.getStatus());
    }

    @Test
    void update_withValidConfig_shouldUpdateSuccessfully() {
        ChannelConfig updatedConfig = ChannelConfig.create(100L, 1L, "更新配置", "{\"key\":\"new-value\"}");
        updatedConfig.setId(1L);
        updatedConfig.disable();
        updatedConfig.setAsDefault();

        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenReturn(testConfig);

        ChannelConfig result = service.update(1L, updatedConfig);

        assertNotNull(result);
        verify(channelConfigRepository).findById(1L);
        verify(channelConfigRepository).save(any(ChannelConfig.class));
    }

    @Test
    void update_withNonExistingConfig_shouldThrowException() {
        when(channelConfigRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.update(1L, testConfig));
    }

    @Test
    void delete_withExistingConfig_shouldDeleteSuccessfully() {
        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);
        doNothing().when(channelConfigRepository).removeById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(channelConfigRepository).removeById(1L);
    }

    @Test
    void delete_withNonExistingConfig_shouldThrowException() {
        when(channelConfigRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.delete(1L));
    }

    @Test
    void findById_withExistingConfig_shouldReturnConfig() {
        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);

        ChannelConfig result = service.findById(1L);

        assertNotNull(result);
        assertEquals("默认配置", result.getConfigName());
    }

    @Test
    void findById_withNonExistingConfig_shouldThrowException() {
        when(channelConfigRepository.findById(1L)).thenReturn(null);

        assertThrows(MessageException.class, () -> service.findById(1L));
    }

    @Test
    void findByOrgId_shouldReturnConfigList() {
        ChannelConfig config2 = ChannelConfig.create(100L, 2L, "配置2", "{\"key\":\"value2\"}");
        config2.setId(2L);
        
        when(channelConfigRepository.findByOrgId(100L)).thenReturn(Arrays.asList(testConfig, config2));

        List<ChannelConfig> result = service.findByOrgId(100L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByChannelId_shouldReturnConfigList() {
        when(channelConfigRepository.findByChannelId(1L)).thenReturn(Arrays.asList(testConfig));

        List<ChannelConfig> result = service.findByChannelId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByOrgIdAndChannelId_shouldReturnConfigList() {
        when(channelConfigRepository.findByOrgIdAndChannelId(100L, 1L)).thenReturn(Arrays.asList(testConfig));

        List<ChannelConfig> result = service.findByOrgIdAndChannelId(100L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByOrgIdAndChannelIdAndConfigName_shouldReturnConfig() {
        when(channelConfigRepository.findByOrgIdAndChannelIdAndConfigName(100L, 1L, "默认配置")).thenReturn(Optional.of(testConfig));

        ChannelConfig result = service.findByOrgIdAndChannelIdAndConfigName(100L, 1L, "默认配置");

        assertNotNull(result);
        assertEquals("默认配置", result.getConfigName());
    }

    @Test
    void enable_shouldUpdateConfigStatus() {
        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenReturn(testConfig);

        assertDoesNotThrow(() -> service.enable(1L));

        verify(channelConfigRepository).save(any(ChannelConfig.class));
    }

    @Test
    void disable_shouldUpdateConfigStatus() {
        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenReturn(testConfig);

        assertDoesNotThrow(() -> service.disable(1L));

        verify(channelConfigRepository).save(any(ChannelConfig.class));
    }

    @Test
    void setAsDefault_shouldUpdateConfigIsDefault() {
        when(channelConfigRepository.findById(1L)).thenReturn(testConfig);
        when(channelConfigRepository.save(any(ChannelConfig.class))).thenReturn(testConfig);

        assertDoesNotThrow(() -> service.setAsDefault(1L));

        verify(channelConfigRepository).save(any(ChannelConfig.class));
    }
}