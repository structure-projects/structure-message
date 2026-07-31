package com.structure.message.core.service;

import com.structure.message.common.plugin.MessageChannelConfig;

/**
 * 通道配置读取服务接口
 * 
 * 职责：
 * 1. 支持同一通道多个配置
 * 2. 支持默认配置（configName为null或空的配置）
 * 3. 提供统一的配置读取入口
 */
public interface ChannelConfigReader {

    /**
     * 获取指定配置名称的配置
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称（可为null，此时获取默认配置）
     * @return 通道配置
     */
    MessageChannelConfig getConfig(String channelCode, Long orgId, String configName);

    /**
     * 获取默认配置（configName为null的配置）
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @return 默认通道配置
     */
    MessageChannelConfig getDefaultConfig(String channelCode, Long orgId);

    /**
     * 检查指定配置名称是否存在
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称
     * @return 是否存在
     */
    boolean configExists(String channelCode, Long orgId, String configName);

    /**
     * 获取配置缓存Key
     * 
     * @param channelCode 通道编码
     * @param orgId 组织ID
     * @param configName 配置名称
     * @return 缓存Key
     */
    String buildConfigCacheKey(String channelCode, Long orgId, String configName);
}