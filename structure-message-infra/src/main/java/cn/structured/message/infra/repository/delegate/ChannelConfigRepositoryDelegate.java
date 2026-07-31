package cn.structured.message.infra.repository.delegate;

import cn.structure.infra.repository.RepositoryDelegate;
import cn.structured.message.domain.entity.ChannelConfig;

import java.util.List;
import java.util.Optional;

public interface ChannelConfigRepositoryDelegate extends RepositoryDelegate<ChannelConfig, Long> {

    List<ChannelConfig> findByOrgId(Long orgId);

    List<ChannelConfig> findByChannelId(Long channelId);

    List<ChannelConfig> findByOrgIdAndChannelId(Long orgId, Long channelId);

    Optional<ChannelConfig> findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName);

    Optional<ChannelConfig> findByOrgIdAndChannelIdAndIsDefault(Long orgId, Long channelId, Integer isDefault);

    List<ChannelConfig> findByStatus(Integer status);

    boolean existsByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName);
}