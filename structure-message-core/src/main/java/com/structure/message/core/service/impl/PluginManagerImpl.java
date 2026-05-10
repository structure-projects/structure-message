package com.structure.message.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.exception.MessageException;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.mapper.ChannelConfigMapper;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.mapper.MessageRecordMapper;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.plugin.MessageChannelPlugin;
import com.structure.message.core.plugin.PluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件管理器实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginManagerImpl implements PluginManager {

    private final MessageChannelMapper channelMapper;
    private final MessageRecordMapper messageRecordMapper;
    private final ChannelConfigMapper configMapper;

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, MessageChannelPlugin> pluginRegistry = new ConcurrentHashMap<>();

    private final Map<String, Boolean> orgPluginStatusCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("开始自动注册所有消息通道插件");
        
        // 获取所有 MessageChannelPlugin 类型的 Bean
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
    }

    @Override
    public MessageChannelPlugin getPlugin(String channelCode) {
        return pluginRegistry.get(channelCode);
    }

    @Override
    public List<MessageChannelPlugin> getAllPlugins() {
        return new ArrayList<>(pluginRegistry.values());
    }

    @Override
    public void enablePlugin(String channelCode, Long orgId) {
        log.info("启用插件，通道编码：{}，组织ID：{}", channelCode, orgId);

        String cacheKey = buildCacheKey(channelCode, orgId);
        orgPluginStatusCache.put(cacheKey, true);
    }

    @Override
    public void disablePlugin(String channelCode, Long orgId) {
        log.info("禁用插件，通道编码：{}，组织ID：{}", channelCode, orgId);

        String cacheKey = buildCacheKey(channelCode, orgId);
        orgPluginStatusCache.put(cacheKey, false);
    }

    @Override
    public boolean isPluginEnabled(String channelCode, Long orgId) {
        log.debug("检查插件状态，通道编码：{}，组织ID：{}", channelCode, orgId);

        if (!pluginRegistry.containsKey(channelCode)) {
            log.warn("插件未注册，通道编码：{}，将尝试重新加载", channelCode);
            return false;
        }

        // 如果有组织ID，确保插件已经初始化过
        if (orgId != null) {
            ensurePluginInitialized(channelCode, orgId);
        }

        String cacheKey = buildCacheKey(channelCode, orgId);
        Boolean cachedStatus = orgPluginStatusCache.get(cacheKey);
        if (cachedStatus != null) {
            return cachedStatus;
        }

        MessageChannelEntity channel = channelMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MessageChannelEntity>()
                .eq(MessageChannelEntity::getChannelCode, channelCode)
        );

        if (channel == null || MessageConstants.ChannelStatus.DISABLED == channel.getStatus()) {
            orgPluginStatusCache.put(cacheKey, false);
            return false;
        }

        orgPluginStatusCache.put(cacheKey, true);
        return true;
    }

    private void ensurePluginInitialized(String channelCode, Long orgId) {
        MessageChannelPlugin plugin = pluginRegistry.get(channelCode);
        if (plugin != null && !plugin.isHealthy()) {
            try {
                log.info("初始化插件，通道编码：{}，组织ID：{}", channelCode, orgId);
                MessageChannelConfig config = getChannelConfig(channelCode, orgId);
                plugin.initialize(config);
            } catch (Exception e) {
                log.error("插件初始化失败，通道编码：{}", channelCode, e);
            }
        }
    }

    @Override
    @CacheEvict(value = "pluginStatus", key = "#channelCode + ':' + #orgId")
    public void reloadPluginConfig(String channelCode, Long orgId) {
        log.info("重新加载插件配置，通道编码：{}，组织ID：{}", channelCode, orgId);

        String cacheKey = buildCacheKey(channelCode, orgId);
        orgPluginStatusCache.remove(cacheKey);

        MessageChannelPlugin plugin = pluginRegistry.get(channelCode);
        if (plugin != null) {
            try {
                MessageChannelConfig config = getChannelConfig(channelCode, orgId);
                plugin.initialize(config);
                log.info("插件配置重新加载成功，通道编码：{}", channelCode);
            } catch (Exception e) {
                log.error("插件配置重新加载失败，通道编码：{}", channelCode, e);
            }
        }
    }

    @Override
    public List<MessageChannelPlugin> getEnabledPlugins(Long orgId) {
        return pluginRegistry.entrySet().stream()
                .filter(entry -> isPluginEnabled(entry.getKey(), orgId))
                .map(Map.Entry::getValue)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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

        // 先注册插件到内存，不初始化配置（因为没有组织ID）
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

    private MessageChannelConfig getChannelConfig(String channelCode, Long orgId) {
        // 从数据库获取通道ID
        MessageChannelEntity channel = channelMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MessageChannelEntity>()
                .eq(MessageChannelEntity::getChannelCode, channelCode)
        );
        
        Long channelId = channel != null ? channel.getId() : null;
        
        // 从数据库获取配置
        Map<String, String> configMap = new HashMap<>();
        if (orgId != null && channelId != null) {
            List<ChannelConfigEntity> configs = configMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChannelConfigEntity>()
                    .eq(ChannelConfigEntity::getOrgId, orgId)
                    .eq(ChannelConfigEntity::getChannelId, channelId)
                    .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED)
            );
            
            for (ChannelConfigEntity config : configs) {
                if (config.getConfigValue() != null) {
                    try {
                        // 解析 JSON 配置
                        JSONObject json = JSON.parseObject(config.getConfigValue());
                        for (String key : json.keySet()) {
                            configMap.put(key, json.getString(key));
                        }
                    } catch (Exception e) {
                        log.error("解析配置失败，配置ID：{}", config.getId(), e);
                    }
                }
            }
        }
        
        log.info("加载通道配置，通道编码：{}，组织ID：{}，配置数量：{}", 
            channelCode, orgId, configMap.size());
        
        return new DefaultMessageChannelConfig(orgId, channelId, channelCode, configMap);
    }

    private String buildCacheKey(String channelCode, Long orgId) {
        return channelCode + ":" + orgId;
    }

    private static class DefaultMessageChannelConfig implements MessageChannelConfig {
        private final String channelCode;
        private final Long orgId;
        private final Long channelId;
        private final Map<String, String> configs;

        public DefaultMessageChannelConfig(Long orgId, Long channelId, String channelCode, Map<String, String> configs) {
            this.channelCode = channelCode;
            this.orgId = orgId;
            this.channelId = channelId;
            this.configs = configs != null ? configs : new HashMap<>();
        }

        @Override
        public Long getChannelId() {
            return channelId;
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
        public String getChannelCode() {
            return channelCode;
        }

        @Override
        public boolean isEncrypted(String key) {
            return false;
        }
    }
}
