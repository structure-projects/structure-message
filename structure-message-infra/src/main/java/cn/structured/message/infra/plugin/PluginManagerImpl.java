package cn.structured.message.infra.plugin;

import cn.structured.message.common.plugin.MessageChannelPlugin;
import cn.structured.message.domain.plugin.PluginManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件管理器实现类
 * <p>
 * 使用ConcurrentHashMap存储已注册的消息通道插件，支持线程安全的注册、获取和注销操作。
 * </p>
 */
@Slf4j
public class PluginManagerImpl implements PluginManager {

    /**
     * 插件缓存，使用通道编码作为key
     */
    private final Map<String, MessageChannelPlugin> pluginCache = new ConcurrentHashMap<>();

    /**
     * 获取消息通道插件
     * <p>
     * 根据通道编码从缓存中获取插件。
     * </p>
     *
     * @param channelCode 通道编码
     * @param orgId       机构ID
     * @param configName  配置名称
     * @return 消息通道插件，如果未找到则返回null
     */
    @Override
    public MessageChannelPlugin getPlugin(String channelCode, Long orgId, String configName) {
        if (channelCode == null) {
            return null;
        }
        return pluginCache.get(channelCode);
    }

    /**
     * 判断插件是否启用
     * <p>
     * 检查通道编码对应的插件是否已注册。
     * </p>
     *
     * @param channelCode 通道编码
     * @param orgId       机构ID
     * @param configName  配置名称
     * @return true-已启用，false-未启用
     */
    @Override
    public boolean isPluginEnabled(String channelCode, Long orgId, String configName) {
        if (channelCode == null) {
            return false;
        }
        return pluginCache.containsKey(channelCode);
    }

    /**
     * 注册消息通道插件
     * <p>
     * 将插件添加到缓存中，并记录日志。
     * </p>
     *
     * @param channelCode 通道编码
     * @param plugin      消息通道插件
     */
    @Override
    public void registerPlugin(String channelCode, MessageChannelPlugin plugin) {
        pluginCache.put(channelCode, plugin);
        log.info("插件注册成功: {}", channelCode);
    }

    /**
     * 注销消息通道插件
     * <p>
     * 从缓存中移除插件，并记录日志。
     * </p>
     *
     * @param channelCode 通道编码
     */
    @Override
    public void unregisterPlugin(String channelCode) {
        pluginCache.remove(channelCode);
        log.info("插件注销成功: {}", channelCode);
    }
}