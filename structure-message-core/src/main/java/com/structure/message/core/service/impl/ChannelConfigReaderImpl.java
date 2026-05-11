package com.structure.message.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.structure.message.common.exception.MessageException;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.mapper.ChannelConfigMapper;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.service.ChannelConfigReader;
import com.structure.message.common.constant.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通道配置读取服务实现类
 * 
 * 核心特性：
 * 1. 支持同一通道多个配置（通过configName区分）
 * 2. 支持默认配置（configName为null或空的配置）
 * 3. 配置查找优先级：指定配置名 -> 默认配置
 * 4. 提供统一的配置读取入口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelConfigReaderImpl implements ChannelConfigReader {

    private final MessageChannelMapper channelMapper;
    private final ChannelConfigMapper configMapper;

    /**
     * 获取指定配置名称的配置
     * 
     * 查找逻辑：
     * 1. 如果指定了configName，先查找该名称的配置
     * 2. 如果未找到或configName为null/空，则查找默认配置（configName为null的配置）
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称（可为null，此时获取默认配置）
     * @return 通道配置
     */
    @Override
    public MessageChannelConfig getConfig(String channelCode, Long orgId, String configName) {
        log.debug("获取通道配置，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);

        // 获取通道信息
        MessageChannelEntity channel = getChannel(channelCode);
        if (channel == null) {
            throw new MessageException("CHANNEL_NOT_FOUND", "通道不存在：" + channelCode);
        }

        Long channelId = channel.getId();
        Map<String, String> configMap = new HashMap<>();
        String resolvedConfigName = null;

        if (orgId != null && channelId != null) {
            // 1. 先尝试查找指定名称的配置
            if (configName != null && !configName.trim().isEmpty()) {
                List<ChannelConfigEntity> namedConfigs = findConfigs(orgId, channelId, configName);
                if (!namedConfigs.isEmpty()) {
                    configMap = parseConfigs(namedConfigs);
                    resolvedConfigName = configName;
                } else {
                    log.warn("指定的配置名称不存在，将尝试查找默认配置，通道编码：{}，配置名称：{}", channelCode, configName);
                    // 回退到默认配置
                    List<ChannelConfigEntity> defaultConfigs = findDefaultConfigs(orgId, channelId);
                    if (!defaultConfigs.isEmpty()) {
                        configMap = parseConfigs(defaultConfigs);
                        resolvedConfigName = null;
                    }
                }
            } else {
                // 直接查找默认配置
                List<ChannelConfigEntity> defaultConfigs = findDefaultConfigs(orgId, channelId);
                if (!defaultConfigs.isEmpty()) {
                    configMap = parseConfigs(defaultConfigs);
                    resolvedConfigName = null;
                }
            }
        }

        log.info("加载通道配置完成，通道编码：{}，组织ID：{}，配置名称：{}，配置项数量：{}",
                channelCode, orgId, resolvedConfigName, configMap.size());

        return new MessageChannelConfigImpl(orgId, channelId, channelCode, resolvedConfigName, configMap);
    }

    /**
     * 获取默认配置（configName为null的配置）
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @return 默认通道配置
     */
    @Override
    public MessageChannelConfig getDefaultConfig(String channelCode, Long orgId) {
        return getConfig(channelCode, orgId, null);
    }

    /**
     * 检查指定配置名称是否存在
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称
     * @return 是否存在
     */
    @Override
    public boolean configExists(String channelCode, Long orgId, String configName) {
        try {
            MessageChannelEntity channel = getChannel(channelCode);
            if (channel == null || orgId == null) {
                return false;
            }

            List<ChannelConfigEntity> configs;
            if (configName != null && !configName.trim().isEmpty()) {
                configs = findConfigs(orgId, channel.getId(), configName);
            } else {
                configs = findDefaultConfigs(orgId, channel.getId());
            }

            return !configs.isEmpty();
        } catch (Exception e) {
            log.error("检查配置存在性失败，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName, e);
            return false;
        }
    }

    /**
     * 获取配置缓存Key
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称
     * @return 缓存Key
     */
    @Override
    public String buildConfigCacheKey(String channelCode, Long orgId, String configName) {
        return channelCode + ":" + orgId + ":" + (configName != null ? configName : "default");
    }

    /**
     * 获取通道信息
     */
    private MessageChannelEntity getChannel(String channelCode) {
        return channelMapper.selectOne(
            new LambdaQueryWrapper<MessageChannelEntity>()
                .eq(MessageChannelEntity::getChannelCode, channelCode)
        );
    }

    /**
     * 查找指定名称的配置
     */
    private List<ChannelConfigEntity> findConfigs(Long orgId, Long channelId, String configName) {
        return configMapper.selectList(
            new LambdaQueryWrapper<ChannelConfigEntity>()
                .eq(ChannelConfigEntity::getOrgId, orgId)
                .eq(ChannelConfigEntity::getChannelId, channelId)
                .eq(ChannelConfigEntity::getConfigName, configName)
                .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED)
        );
    }

    /**
     * 查找默认配置
     * 
     * 默认配置查找优先级：
     * 1. isDefault = 1 的配置（新方式）
     * 2. configName 为 null 的配置（兼容旧方式）
     */
    private List<ChannelConfigEntity> findDefaultConfigs(Long orgId, Long channelId) {
        return configMapper.selectList(
            new LambdaQueryWrapper<ChannelConfigEntity>()
                .eq(ChannelConfigEntity::getOrgId, orgId)
                .eq(ChannelConfigEntity::getChannelId, channelId)
                .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED)
                .eq(ChannelConfigEntity::getIsDefault, 1)
        );
    }

    /**
     * 解析配置列表为Map
     */
    private Map<String, String> parseConfigs(List<ChannelConfigEntity> configs) {
        Map<String, String> configMap = new HashMap<>();
        for (ChannelConfigEntity config : configs) {
            if (config.getConfigValue() != null) {
                try {
                    JSONObject json = JSON.parseObject(config.getConfigValue());
                    for (String key : json.keySet()) {
                        configMap.put(key, json.getString(key));
                    }
                } catch (Exception e) {
                    log.error("解析配置失败，配置ID：{}", config.getId(), e);
                }
            }
        }
        return Collections.unmodifiableMap(configMap);
    }
}