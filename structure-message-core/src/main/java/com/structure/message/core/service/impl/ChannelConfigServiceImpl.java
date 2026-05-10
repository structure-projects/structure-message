package com.structure.message.core.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.structured.mybatis.plus.starter.base.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.exception.MessageException;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.mapper.ChannelConfigMapper;
import com.structure.message.core.service.ChannelConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final String ENCRYPT_KEY = "message-center-encrypt-key-12345678";
    private final AES aes = SecureUtil.aes(ENCRYPT_KEY.getBytes());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(ChannelConfigEntity config) {
        log.info("保存组织通道配置，组织ID：{}，通道ID：{}", config.getOrgId(), config.getChannelId());

        config.setStatus(MessageConstants.ChannelStatus.ENABLED);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());

        this.saveOrUpdate( config);

        log.info("组织通道配置保存成功，ID：{}", config.getId());
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
        // 缓存会自动清除，下次访问时重新加载
    }

}

