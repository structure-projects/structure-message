package cn.structured.message.infra.repository;

import cn.structure.infra.repository.RepositoryFacade;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.repository.ChannelConfigRepository;
import cn.structured.message.infra.repository.delegate.ChannelConfigRepositoryDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("channelConfigRepository")
public class ChannelConfigRepositoryImpl extends RepositoryFacade<ChannelConfig, Long, ChannelConfigRepositoryDelegate> implements ChannelConfigRepository {

    @Override
    public List<ChannelConfig> findByOrgId(Long orgId) {
        return getDelegate().findByOrgId(orgId);
    }

    @Override
    public List<ChannelConfig> findByChannelId(Long channelId) {
        return getDelegate().findByChannelId(channelId);
    }

    @Override
    public List<ChannelConfig> findByOrgIdAndChannelId(Long orgId, Long channelId) {
        return getDelegate().findByOrgIdAndChannelId(orgId, channelId);
    }

    @Override
    public Optional<ChannelConfig> findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName) {
        return getDelegate().findByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName);
    }

    @Override
    public Optional<ChannelConfig> findByOrgIdAndChannelIdAndIsDefault(Long orgId, Long channelId, Integer isDefault) {
        return getDelegate().findByOrgIdAndChannelIdAndIsDefault(orgId, channelId, isDefault);
    }

    @Override
    public List<ChannelConfig> findByStatus(Integer status) {
        return getDelegate().findByStatus(status);
    }

    @Override
    public boolean existsByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName) {
        return getDelegate().existsByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName);
    }
}