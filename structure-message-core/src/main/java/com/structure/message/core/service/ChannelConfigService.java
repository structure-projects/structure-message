package com.structure.message.core.service;

import cn.structured.mybatis.plus.starter.base.IBaseService;
import com.structure.message.core.domain.entity.ChannelConfigEntity;

import java.util.List;
import java.util.Map;

/**
 * 组织通道配置服务接口
 */
public interface ChannelConfigService extends IBaseService<ChannelConfigEntity> {

    /**
     * 保存组织通道配置
     */
    void saveConfig(ChannelConfigEntity config);

    /**
     * 获取组织通道所有配置
     */
    List<ChannelConfigEntity> getConfigs(Long orgId);

    /**
     * 启用组织通道配置
     */
    void enableConfig(Long configId);

    /**
     * 禁用组织通道配置
     */
    void disableConfig(Long configId);


    /**
     * 重新加载组织通道配置
     */
    void reloadConfig(Long orgId, Long channelId);

}