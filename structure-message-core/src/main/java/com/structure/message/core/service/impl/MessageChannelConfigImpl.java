package com.structure.message.core.service.impl;

import com.structure.message.common.plugin.MessageChannelConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认消息通道配置实现
 */
public class MessageChannelConfigImpl implements MessageChannelConfig {
    private final Long orgId;
    private final Long channelId;
    private final String channelCode;
    private final Map<String, String> configs;

    public MessageChannelConfigImpl(Long orgId, Long channelId, String channelCode, Map<String, String> configs) {
        this.orgId = orgId;
        this.channelId = channelId;
        this.channelCode = channelCode;
        this.configs = configs != null ? configs : new HashMap<>();
    }

    @Override
    public String getConfig(String key) {
        return configs.get(key);
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        return configs.getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, String> getAllConfigs() {
        return new HashMap<>(configs);
    }

    @Override
    public Long getOrgId() {
        return orgId;
    }

    @Override
    public Long getChannelId() {
        return channelId;
    }

    @Override
    public String getChannelCode() {
        return channelCode;
    }

    @Override
    public boolean isEncrypted(String key) {
        // 这里可以根据实际需求判断配置是否加密
        return false;
    }
}
