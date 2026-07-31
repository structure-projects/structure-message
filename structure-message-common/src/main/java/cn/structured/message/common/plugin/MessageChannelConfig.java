package cn.structured.message.common.plugin;

import java.util.Map;

/**
 * 消息通道配置接口
 */
public interface MessageChannelConfig {

    /**
     * 获取配置值
     */
    String getConfig(String key);

    /**
     * 获取配置值，带默认值
     */
    String getConfig(String key, String defaultValue);

    /**
     * 获取所有配置
     */
    Map<String, String> getAllConfigs();

    /**
     * 获取组织ID
     */
    Long getOrgId();

    /**
     * 获取通道ID
     */
    Long getChannelId();

    /**
     * 获取通道编码
     */
    String getChannelCode();

    /**
     * 获取配置名称（用于区分同一通道的多个配置）
     */
    String getConfigName();

}