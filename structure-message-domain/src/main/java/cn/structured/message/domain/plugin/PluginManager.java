package cn.structured.message.domain.plugin;

import cn.structured.message.common.plugin.MessageChannelPlugin;

/**
 * 插件管理器接口
 * <p>
 * 定义消息通道插件的注册、获取和注销操作，由基础设施层实现。
 * </p>
 */
public interface PluginManager {

    /**
     * 获取消息通道插件
     *
     * @param channelCode 通道编码
     * @param orgId       机构ID
     * @param configName  配置名称
     * @return 消息通道插件，如果未找到则返回null
     */
    MessageChannelPlugin getPlugin(String channelCode, Long orgId, String configName);

    /**
     * 判断插件是否启用
     *
     * @param channelCode 通道编码
     * @param orgId       机构ID
     * @param configName  配置名称
     * @return true-已启用，false-未启用
     */
    boolean isPluginEnabled(String channelCode, Long orgId, String configName);

    /**
     * 注册消息通道插件
     *
     * @param channelCode 通道编码
     * @param plugin      消息通道插件
     */
    void registerPlugin(String channelCode, MessageChannelPlugin plugin);

    /**
     * 注销消息通道插件
     *
     * @param channelCode 通道编码
     */
    void unregisterPlugin(String channelCode);
}