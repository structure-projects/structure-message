package com.structure.message.core.service.impl;

import com.structure.message.common.plugin.MessageChannelConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息通道配置实现类
 * 
 * 支持同一通道多个配置，通过 configName 区分
 * - configName 为 null 表示默认配置
 * - configName 不为 null 表示指定名称的配置
 */
public class MessageChannelConfigImpl implements MessageChannelConfig {
    private final Long orgId;
    private final Long channelId;
    private final String channelCode;
    private final String configName;
    private final Map<String, String> configs;

    /**
     * 创建默认配置（configName 为 null）
     */
    public MessageChannelConfigImpl(Long orgId, Long channelId, String channelCode, Map<String, String> configs) {
        this(orgId, channelId, channelCode, null, configs);
    }

    /**
     * 创建指定名称的配置
     * 
     * @param orgId 组织ID
     * @param channelId 通道ID
     * @param channelCode 通道编码
     * @param configName 配置名称（null 表示默认配置）
     * @param configs 配置键值对
     */
    public MessageChannelConfigImpl(Long orgId, Long channelId, String channelCode, String configName, Map<String, String> configs) {
        this.orgId = orgId;
        this.channelId = channelId;
        this.channelCode = channelCode;
        this.configName = configName;
        this.configs = configs != null ? new HashMap<>(configs) : new HashMap<>();
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
        return Collections.unmodifiableMap(configs);
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
    public String getConfigName() {
        return configName;
    }

    /**
     * 判断是否为默认配置
     */
    public boolean isDefault() {
        return configName == null;
    }

    @Override
    public String toString() {
        return String.format("MessageChannelConfigImpl{channelCode='%s', orgId=%d, configName='%s', configCount=%d}",
                channelCode, orgId, configName, configs.size());
    }
}
