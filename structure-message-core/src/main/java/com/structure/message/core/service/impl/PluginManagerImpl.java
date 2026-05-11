package com.structure.message.core.service.impl;

import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.exception.MessageException;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.mapper.MessageRecordMapper;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.plugin.MessageChannelPlugin;
import com.structure.message.core.plugin.PluginManager;
import com.structure.message.core.service.ChannelConfigReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PluginManagerImpl implements PluginManager {

    private final MessageChannelMapper channelMapper;
    private final MessageRecordMapper messageRecordMapper;
    private final ChannelConfigReader channelConfigReader;

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, MessageChannelPlugin> pluginRegistry = new ConcurrentHashMap<>();

    private final Map<String, MessageChannelPlugin> orgPluginInstanceCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("开始自动注册所有消息通道插件");

        Map<String, MessageChannelPlugin> plugins = applicationContext.getBeansOfType(MessageChannelPlugin.class);

        for (Map.Entry<String, MessageChannelPlugin> entry : plugins.entrySet()) {
            String beanName = entry.getKey();
            MessageChannelPlugin plugin = entry.getValue();

            try {
                String channelCode = plugin.getChannelCode();
                if (channelCode != null && !channelCode.isEmpty()) {
                    registerPlugin(channelCode, plugin);
                    log.info("自动注册插件成功，Bean名称：{}，通道编码：{}", beanName, channelCode);
                }
            } catch (Exception e) {
                log.error("自动注册插件失败，Bean名称：{}", beanName, e);
            }
        }

        log.info("插件自动注册完成，共注册 {} 个插件", pluginRegistry.size());
    }

    @Override
    public void registerPlugin(String channelCode, MessageChannelPlugin plugin) {
        log.info("注册插件，通道编码：{}，插件类：{}", channelCode, plugin.getClass().getName());

        if (pluginRegistry.containsKey(channelCode)) {
            throw new MessageException("PLUGIN_ALREADY_EXISTS", "插件已存在，通道编码：" + channelCode);
        }

        pluginRegistry.put(channelCode, plugin);
        log.info("插件注册成功，通道编码：{}", channelCode);
    }

    @Override
    public void unregisterPlugin(String channelCode) {
        log.info("卸载插件，通道编码：{}", channelCode);

        MessageChannelPlugin plugin = pluginRegistry.remove(channelCode);
        if (plugin != null) {
            try {
                plugin.destroy();
                log.info("插件卸载成功，通道编码：{}", channelCode);
            } catch (Exception e) {
                log.error("插件销毁失败，通道编码：{}", channelCode, e);
            }
        }

        String pattern = channelCode + ":*";
        orgPluginInstanceCache.keySet().removeIf(key -> key.startsWith(pattern));
    }

    @Override
    public MessageChannelPlugin getPlugin(String channelCode) {
        return pluginRegistry.get(channelCode);
    }

    public MessageChannelPlugin getPlugin(String channelCode, Long orgId, String configName) {
        if (!pluginRegistry.containsKey(channelCode)) {
            throw new MessageException("PLUGIN_NOT_FOUND", "插件未注册，通道编码：" + channelCode);
        }

        String cacheKey = channelConfigReader.buildConfigCacheKey(channelCode, orgId, configName);
        log.info("获取组织级插件缓存，缓存键：{}", cacheKey);
        MessageChannelPlugin cachedPlugin = orgPluginInstanceCache.get(cacheKey);

        if (cachedPlugin != null && cachedPlugin.isHealthy()) {
            return cachedPlugin;
        }

        MessageChannelPlugin prototypePlugin = pluginRegistry.get(channelCode);
        MessageChannelPlugin newInstance = createPluginInstance(prototypePlugin);

        try {
            MessageChannelConfig config = channelConfigReader.getConfig(channelCode, orgId, configName);
            newInstance.initialize(config);
            orgPluginInstanceCache.put(cacheKey, newInstance);
            log.info("创建组织级插件实例成功，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);
        } catch (Exception e) {
            log.error("初始化组织级插件实例失败，通道编码：{}，组织ID：{}", channelCode, orgId, e);
            throw new MessageException("PLUGIN_INIT_ERROR", "插件初始化失败：" + e.getMessage());
        }

        return newInstance;
    }

    private MessageChannelPlugin createPluginInstance(MessageChannelPlugin prototype) {
        try {
            Class<? extends MessageChannelPlugin> pluginClass = prototype.getClass();
            MessageChannelPlugin newInstance = applicationContext.getAutowireCapableBeanFactory().createBean(pluginClass);
            log.debug("通过 Spring BeanFactory 创建插件实例成功，通道编码：{}", prototype.getChannelCode());
            return newInstance;
        } catch (Exception e) {
            log.warn("无法通过 Spring 创建插件实例，将使用原型实例，通道编码：{}，错误：{}", prototype.getChannelCode(), e.getMessage());
            return prototype;
        }
    }

    @Override
    public List<MessageChannelPlugin> getAllPlugins() {
        return new ArrayList<>(pluginRegistry.values());
    }

    @Override
    public void enablePlugin(String channelCode, Long orgId) {
        enablePlugin(channelCode, orgId, null);
    }

    public void enablePlugin(String channelCode, Long orgId, String configName) {
        log.info("启用插件，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);
    }

    @Override
    public void disablePlugin(String channelCode, Long orgId) {
        disablePlugin(channelCode, orgId, null);
    }

    public void disablePlugin(String channelCode, Long orgId, String configName) {
        log.info("禁用插件，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);

        String cacheKey = channelConfigReader.buildConfigCacheKey(channelCode, orgId, configName);
        MessageChannelPlugin plugin = orgPluginInstanceCache.remove(cacheKey);
        if (plugin != null) {
            try {
                plugin.destroy();
            } catch (Exception e) {
                log.error("销毁插件实例失败", e);
            }
        }
    }

    @Override
    public boolean isPluginEnabled(String channelCode, Long orgId) {
        return isPluginEnabled(channelCode, orgId, null);
    }

    public boolean isPluginEnabled(String channelCode, Long orgId, String configName) {
        log.debug("检查插件状态，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);

        if (!pluginRegistry.containsKey(channelCode)) {
            log.warn("插件未注册，通道编码：{}", channelCode);
            return false;
        }

        MessageChannelEntity channel = channelMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MessageChannelEntity>()
                .eq(MessageChannelEntity::getChannelCode, channelCode)
        );

        if (channel == null || MessageConstants.ChannelStatus.DISABLED == channel.getStatus()) {
            return false;
        }

        if (orgId != null) {
            return channelConfigReader.configExists(channelCode, orgId, configName);
        }

        return true;
    }

    @Override
    @CacheEvict(value = "pluginStatus", key = "#channelCode + ':' + #orgId")
    public void reloadPluginConfig(String channelCode, Long orgId) {
        reloadPluginConfig(channelCode, orgId, null);
    }

    public void reloadPluginConfig(String channelCode, Long orgId, String configName) {
        log.info("重新加载插件配置，通道编码：{}，组织ID：{}，配置名称：{}", channelCode, orgId, configName);

        String cacheKey = channelConfigReader.buildConfigCacheKey(channelCode, orgId, configName);
        MessageChannelPlugin plugin = orgPluginInstanceCache.remove(cacheKey);
        if (plugin != null) {
            try {
                plugin.destroy();
            } catch (Exception e) {
                log.error("销毁旧插件实例失败", e);
            }
        }

        if (orgId != null) {
            getPlugin(channelCode, orgId, configName);
        }
    }

    @Override
    public List<MessageChannelPlugin> getEnabledPlugins(Long orgId) {
        List<MessageChannelPlugin> enabledPlugins = new ArrayList<>();

        for (String channelCode : pluginRegistry.keySet()) {
            if (isPluginEnabled(channelCode, orgId)) {
                try {
                    MessageChannelPlugin plugin = getPlugin(channelCode, orgId, null);
                    enabledPlugins.add(plugin);
                } catch (Exception e) {
                    log.error("获取启用的插件失败，通道编码：{}", channelCode, e);
                }
            }
        }

        return enabledPlugins;
    }

    @Override
    public boolean hasPlugin(String channelCode) {
        return pluginRegistry.containsKey(channelCode);
    }

    public void initializeAllPlugins() {
        log.info("开始初始化所有插件");

        List<MessageChannelEntity> channels = channelMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MessageChannelEntity>()
                .eq(MessageChannelEntity::getStatus, MessageConstants.ChannelStatus.ENABLED)
        );

        for (MessageChannelEntity channel : channels) {
            try {
                MessageChannelPlugin plugin = pluginRegistry.get(channel.getChannelCode());
                if (plugin != null) {
                    log.info("插件已注册，通道编码：{}", channel.getChannelCode());
                }
            } catch (Exception e) {
                log.error("插件注册处理失败，通道编码：{}", channel.getChannelCode(), e);
            }
        }

        log.info("插件初始化完成，实际配置会在第一次使用时加载");
    }
}
