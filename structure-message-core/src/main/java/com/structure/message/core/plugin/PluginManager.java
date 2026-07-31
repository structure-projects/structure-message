package com.structure.message.core.plugin;

import com.structure.message.common.plugin.MessageChannelPlugin;

import java.util.List;

/**
 * 插件管理器
 */
public interface PluginManager {

    /**
     * 注册插件
     */
    void registerPlugin(String channelCode, MessageChannelPlugin plugin);

    /**
     * 卸载插件
     */
    void unregisterPlugin(String channelCode);

    /**
     * 获取插件
     */
    MessageChannelPlugin getPlugin(String channelCode);

    /**
     * 获取所有插件
     */
    List<MessageChannelPlugin> getAllPlugins();

    /**
     * 启用插件
     */
    void enablePlugin(String channelCode, Long orgId);

    /**
     * 禁用插件
     */
    void disablePlugin(String channelCode, Long orgId);

    /**
     * 检查插件是否启用
     */
    boolean isPluginEnabled(String channelCode, Long orgId);

    /**
     * 重新加载插件配置
     */
    void reloadPluginConfig(String channelCode, Long orgId);

    /**
     * 获取组织启用的插件
     */
    List<MessageChannelPlugin> getEnabledPlugins(Long orgId);

    /**
     * 检查插件是否存在
     */
    boolean hasPlugin(String channelCode);
}