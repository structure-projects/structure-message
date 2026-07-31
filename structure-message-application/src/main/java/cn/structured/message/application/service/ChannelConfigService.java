package cn.structured.message.application.service;

import cn.structured.message.domain.entity.ChannelConfig;

import java.util.List;

/**
 * 通道配置服务接口
 * <p>
 * 定义通道配置的业务操作，包括创建、更新、删除、查询等功能。
 * </p>
 */
public interface ChannelConfigService {

    /**
     * 创建通道配置
     *
     * @param config 通道配置实体
     * @return 创建后的通道配置实体
     */
    ChannelConfig create(ChannelConfig config);

    /**
     * 更新通道配置
     *
     * @param id     配置ID
     * @param config 通道配置实体
     * @return 更新后的通道配置实体
     */
    ChannelConfig update(Long id, ChannelConfig config);

    /**
     * 删除通道配置
     *
     * @param id 配置ID
     */
    void delete(Long id);

    /**
     * 根据ID查询通道配置
     *
     * @param id 配置ID
     * @return 通道配置实体
     */
    ChannelConfig findById(Long id);

    /**
     * 根据机构ID查询配置列表
     *
     * @param orgId 机构ID
     * @return 通道配置列表
     */
    List<ChannelConfig> findByOrgId(Long orgId);

    /**
     * 根据通道ID查询配置列表
     *
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    List<ChannelConfig> findByChannelId(Long channelId);

    /**
     * 根据机构ID和通道ID查询配置列表
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    List<ChannelConfig> findByOrgIdAndChannelId(Long orgId, Long channelId);

    /**
     * 根据机构ID、通道ID和配置名称查询配置
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @return 通道配置实体，如果不存在则返回null
     */
    ChannelConfig findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName);

    /**
     * 根据机构ID和通道ID查询默认配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 默认通道配置实体，如果不存在则返回null
     */
    ChannelConfig findDefaultByOrgIdAndChannelId(Long orgId, Long channelId);

    /**
     * 启用通道配置
     *
     * @param id 配置ID
     */
    void enable(Long id);

    /**
     * 禁用通道配置
     *
     * @param id 配置ID
     */
    void disable(Long id);

    /**
     * 设置为默认配置
     *
     * @param id 配置ID
     */
    void setAsDefault(Long id);

    /**
     * 取消默认配置
     *
     * @param id 配置ID
     */
    void unsetAsDefault(Long id);
}