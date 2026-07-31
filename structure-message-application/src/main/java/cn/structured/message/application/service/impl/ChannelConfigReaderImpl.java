package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.ChannelConfigReader;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.repository.ChannelConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通道配置读取服务实现类
 * <p>
 * 实现ChannelConfigReader接口，提供通道配置的只读查询操作。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChannelConfigReaderImpl implements ChannelConfigReader {

    /**
     * 通道配置仓储
     */
    private final ChannelConfigRepository channelConfigRepository;

    /**
     * 获取指定配置
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @return 通道配置实体，如果不存在则返回null
     */
    @Override
    public ChannelConfig getConfig(Long orgId, Long channelId, String configName) {
        return channelConfigRepository.findByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName)
                .orElse(null);
    }

    /**
     * 获取指定机构和通道的所有配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    @Override
    public List<ChannelConfig> getAllConfigs(Long orgId, Long channelId) {
        return channelConfigRepository.findByOrgIdAndChannelId(orgId, channelId);
    }

    /**
     * 获取默认配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 默认通道配置实体，如果不存在则返回null
     */
    @Override
    public ChannelConfig getDefaultConfig(Long orgId, Long channelId) {
        return channelConfigRepository.findByOrgIdAndChannelIdAndIsDefault(orgId, channelId, 1)
                .orElse(null);
    }
}