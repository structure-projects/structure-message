package cn.structured.message.application.assembler;

import cn.structured.message.common.dto.ChannelConfigDTO;
import cn.structured.message.common.vo.OrgChannelConfigVO;
import cn.structured.message.domain.entity.ChannelConfig;

/**
 * 通道配置组装器
 * <p>
 * 负责通道配置领域实体与DTO、VO之间的转换。
 * </p>
 */
public class ChannelConfigAssembler {

    /**
     * 将DTO转换为领域实体
     *
     * @param dto 通道配置DTO
     * @return 通道配置领域实体
     */
    public static ChannelConfig toEntity(ChannelConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        return ChannelConfig.builder()
                .orgId(dto.getOrgId())
                .channelId(dto.getChannelId())
                .configName(dto.getConfigName())
                .configValue(dto.getConfigValue())
                .status(dto.getStatus())
                .isDefault(dto.getIsDefault())
                .build();
    }

    /**
     * 将领域实体转换为VO
     *
     * @param entity 通道配置领域实体
     * @return 通道配置VO
     */
    public static OrgChannelConfigVO toVO(ChannelConfig entity) {
        if (entity == null) {
            return null;
        }
        OrgChannelConfigVO vo = new OrgChannelConfigVO();
        vo.setId(entity.getId());
        vo.setOrgId(entity.getOrgId());
        vo.setChannelId(entity.getChannelId());
        vo.setConfigName(entity.getConfigName());
        vo.setConfigValue(entity.getConfigValue());
        vo.setStatus(entity.getStatus());
        vo.setIsDefault(entity.getIsDefault());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}