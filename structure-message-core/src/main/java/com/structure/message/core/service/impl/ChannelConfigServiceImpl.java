package com.structure.message.core.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.structured.mybatis.plus.starter.base.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.exception.MessageException;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.mapper.ChannelConfigMapper;
import com.structure.message.core.plugin.PluginManager;
import com.structure.message.core.service.ChannelConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织通道配置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelConfigServiceImpl extends BaseServiceImpl<ChannelConfigMapper, ChannelConfigEntity> implements ChannelConfigService {

    private final ChannelConfigMapper configMapper;
    private final MessageChannelMapper channelMapper;

    @Autowired(required = false)
    private PluginManager pluginManager;

    private static final String ENCRYPT_KEY = "message-center-encrypt-key-12345678";
    private final AES aes = SecureUtil.aes(ENCRYPT_KEY.getBytes());

    @Override
    public boolean save(ChannelConfigEntity entity) {
        log.info("保存组织通道配置，组织ID：{}，通道ID：{}", entity.getOrgId(), entity.getChannelId());

        // 验证是否为默认配置
        if (MessageConstants.ChannelStatus.DEFAULT == (entity.getIsDefault())) {
            clearDefaultConfig(entity.getOrgId(), entity.getChannelId());
        }

        // 验证配置名称唯一性
        validateConfigNameUniqueness(entity.getOrgId(), entity.getConfigName());

        entity.setStatus(MessageConstants.ChannelStatus.ENABLED);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        boolean save = super.save(entity);
        log.info("组织通道配置保存成功，ID：{}", entity.getId());
        return save;
    }


    @Override
    public boolean updateById(ChannelConfigEntity entity) {

        // 配置名称不允许修改
        entity.setConfigName(null);

        if (MessageConstants.ChannelStatus.DEFAULT == (entity.getIsDefault())) {
            clearDefaultConfig(entity.getOrgId(), entity.getChannelId());
        }

        return super.updateById(entity);
    }

    /**
     * 验证配置名称唯一性
     *
     * @param orgId       组织ID
     * @param configName  配置名称
     */
    private void validateConfigNameUniqueness(Long orgId, String configName) {
        LambdaQueryWrapper<ChannelConfigEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChannelConfigEntity::getOrgId, orgId)
               .eq(ChannelConfigEntity::getConfigName, configName);

        long count = this.count(wrapper);
        if (count > 0) {
            throw new MessageException("CONFIG_NAME_EXISTS", "该组织下配置名称已存在：'" + configName + "'，请使用其他名称");
        }
    }


    /**
     * 清除默认配置
     *
     * @param orgId       组织ID
     * @param channelId   通道ID
     */
    private void clearDefaultConfig(Long orgId, Long channelId) {
        LambdaQueryWrapper<ChannelConfigEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChannelConfigEntity::getOrgId, orgId)
               .eq(ChannelConfigEntity::getChannelId, channelId)
               .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED);

        ChannelConfigEntity unDefaultConfig = new ChannelConfigEntity() ;
        unDefaultConfig.setIsDefault(0);
        baseMapper.update(unDefaultConfig, wrapper);

    }



    @Override
    @Cacheable(value = "orgChannelConfigs", key = "#orgId")
    public List<ChannelConfigEntity> getConfigs(Long orgId) {
        log.debug("获取组织通道所有配置，组织ID：{}", orgId);

        LambdaQueryWrapper<ChannelConfigEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChannelConfigEntity::getOrgId, orgId)
               .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED);

       return configMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(value = "orgChannelConfigs", key = "#orgId + ':' + #channelId")
    public List<ChannelConfigEntity> getConfigs(Long orgId, Long channelId) {
        log.debug("获取组织指定通道的配置列表，组织ID：{}，通道ID：{}", orgId, channelId);

        LambdaQueryWrapper<ChannelConfigEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChannelConfigEntity::getOrgId, orgId)
               .eq(ChannelConfigEntity::getChannelId, channelId)
               .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED);

        return configMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(value = "orgChannelConfig", key = "#orgId + ':' + #channelId + ':' + #configName")
    public ChannelConfigEntity getConfig(Long orgId, Long channelId, String configName) {
        log.debug("获取组织指定通道的指定配置，组织ID：{}，通道ID：{}，配置名称：{}", orgId, channelId, configName);

        LambdaQueryWrapper<ChannelConfigEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChannelConfigEntity::getOrgId, orgId)
               .eq(ChannelConfigEntity::getChannelId, channelId)
               .eq(ChannelConfigEntity::getStatus, MessageConstants.ChannelStatus.ENABLED);

        if (configName != null && !configName.isEmpty()) {
            wrapper.eq(ChannelConfigEntity::getConfigName, configName);
        } else {
            wrapper.isNull(ChannelConfigEntity::getConfigName);
        }

        return configMapper.selectOne(wrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"orgChannelConfig", "orgChannelConfigs", "orgChannelConfigMap"}, key = "#configId")
    public void enableConfig(Long configId) {
        log.info("启用组织通道配置，ID：{}", configId);

        ChannelConfigEntity config = configMapper.selectById(configId);
        if (config == null) {
            throw new MessageException("CONFIG_NOT_FOUND", "配置不存在：" + configId);
        }

        config.setStatus(MessageConstants.ChannelStatus.ENABLED);
        config.setUpdateTime(LocalDateTime.now());
        configMapper.updateById(config);

        log.info("组织通道配置启用成功，ID：{}", configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"orgChannelConfig", "orgChannelConfigs", "orgChannelConfigMap"}, key = "#configId")
    public void disableConfig(Long configId) {
        log.info("禁用组织通道配置，ID：{}", configId);

        ChannelConfigEntity config = configMapper.selectById(configId);
        if (config == null) {
            throw new MessageException("CONFIG_NOT_FOUND", "配置不存在：" + configId);
        }

        config.setStatus(MessageConstants.ChannelStatus.DISABLED);
        config.setUpdateTime(LocalDateTime.now());
        configMapper.updateById(config);

        log.info("组织通道配置禁用成功，ID：{}", configId);
    }

    @Override
    @CacheEvict(value = {"orgChannelConfigs", "orgChannelConfigMap"}, key = "#orgId + ':' + #channelId")
    public void reloadConfig(Long orgId, Long channelId) {
        log.info("重新加载组织通道配置，组织ID：{}，通道ID：{}", orgId, channelId);

        // 获取通道编码
        MessageChannelEntity channel = channelMapper.selectById(channelId);
        if (channel != null && pluginManager != null) {
            pluginManager.reloadPluginConfig(channel.getChannelCode(), orgId);
        }

        // 缓存会自动清除，下次访问时重新加载
    }

}

