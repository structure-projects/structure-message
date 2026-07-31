package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.infra.repository.delegate.ChannelConfigRepositoryDelegate;
import cn.structured.message.repository.mapper.ChannelConfigMapper;
import cn.structured.message.repository.po.OrgChannelConfigPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@WriteDelegate
public class ChannelConfigRepositoryDelegateImpl extends MybatisPlusRepositoryDelegate<ChannelConfig, OrgChannelConfigPO, Long> implements ChannelConfigRepositoryDelegate {

    @Override
    protected ChannelConfig toEntity(OrgChannelConfigPO po) {
        if (po == null) {
            return null;
        }
        return ChannelConfig.builder()
                .id(po.getId())
                .orgId(po.getOrgId())
                .channelId(po.getChannelId())
                .configName(po.getConfigName())
                .configValue(po.getConfigValue())
                .status(po.getStatus())
                .isDefault(po.getIsDefault())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    @Override
    protected OrgChannelConfigPO toPo(ChannelConfig entity) {
        if (entity == null) {
            return null;
        }
        OrgChannelConfigPO po = new OrgChannelConfigPO();
        po.setId(entity.getId());
        po.setOrgId(entity.getOrgId());
        po.setChannelId(entity.getChannelId());
        po.setConfigName(entity.getConfigName());
        po.setConfigValue(entity.getConfigValue());
        po.setStatus(entity.getStatus());
        po.setIsDefault(entity.getIsDefault());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }

    @Override
    public List<ChannelConfig> findByOrgId(Long orgId) {
        List<OrgChannelConfigPO> pos = ((ChannelConfigMapper) baseMapper).selectByOrgId(orgId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<ChannelConfig> findByChannelId(Long channelId) {
        List<OrgChannelConfigPO> pos = ((ChannelConfigMapper) baseMapper).selectByChannelId(channelId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<ChannelConfig> findByOrgIdAndChannelId(Long orgId, Long channelId) {
        List<OrgChannelConfigPO> pos = ((ChannelConfigMapper) baseMapper).selectByOrgIdAndChannelId(orgId, channelId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public Optional<ChannelConfig> findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName) {
        OrgChannelConfigPO po = ((ChannelConfigMapper) baseMapper).selectByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName);
        return Optional.ofNullable(toEntity(po));
    }

    @Override
    public Optional<ChannelConfig> findByOrgIdAndChannelIdAndIsDefault(Long orgId, Long channelId, Integer isDefault) {
        OrgChannelConfigPO po = ((ChannelConfigMapper) baseMapper).selectByOrgIdAndChannelIdAndIsDefault(orgId, channelId, isDefault);
        return Optional.ofNullable(toEntity(po));
    }

    @Override
    public List<ChannelConfig> findByStatus(Integer status) {
        List<OrgChannelConfigPO> pos = ((ChannelConfigMapper) baseMapper).selectByStatus(status);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public boolean existsByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName) {
        return ((ChannelConfigMapper) baseMapper).countByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName) > 0;
    }
}