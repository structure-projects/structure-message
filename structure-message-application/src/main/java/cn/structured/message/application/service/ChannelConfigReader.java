package cn.structured.message.application.service;

import cn.structured.message.domain.entity.ChannelConfig;

import java.util.List;

/**
 * 通道配置读取服务接口
 * <p>
 * 提供通道配置的只读查询操作，用于消息发送时获取配置信息。
 * </p>
 */
public interface ChannelConfigReader {

    /**
     * 获取指定配置
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @return 通道配置实体，如果不存在则返回null
     */
    ChannelConfig getConfig(Long orgId, Long channelId, String configName);

    /**
     * 获取指定机构和通道的所有配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    List<ChannelConfig> getAllConfigs(Long orgId, Long channelId);

    /**
     * 获取默认配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 默认通道配置实体，如果不存在则返回null
     */
    ChannelConfig getDefaultConfig(Long orgId, Long channelId);
}