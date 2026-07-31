package cn.structured.message.domain.repository;

import cn.structure.common.repository.ICrudRepository;
import cn.structured.message.domain.entity.ChannelConfig;

import java.util.List;
import java.util.Optional;

/**
 * 通道配置仓储接口
 * <p>
 * 定义通道配置领域实体的数据访问操作，由基础设施层实现。
 * </p>
 */
public interface ChannelConfigRepository extends ICrudRepository<ChannelConfig, Long> {

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
     * @return 通道配置实体
     */
    Optional<ChannelConfig> findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName);

    /**
     * 根据机构ID、通道ID和是否默认查询配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @param isDefault 是否默认：0-否，1-是
     * @return 通道配置实体
     */
    Optional<ChannelConfig> findByOrgIdAndChannelIdAndIsDefault(Long orgId, Long channelId, Integer isDefault);

    /**
     * 根据状态查询配置列表
     *
     * @param status 状态：0-禁用，1-启用
     * @return 通道配置列表
     */
    List<ChannelConfig> findByStatus(Integer status);

    /**
     * 判断配置是否存在
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @return true-存在，false-不存在
     */
    boolean existsByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName);
}