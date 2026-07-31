package com.structure.message.core.domain.assembler;

import com.structure.message.core.domain.dto.ChannelConfigDTO;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.domain.vo.OrgChannelConfigVO;
import org.springframework.beans.BeanUtils;

import static cn.hutool.json.XMLTokener.entity;

/**
 * 组织通道配置Assembler
 * @author chuck
 * @version 2024/07/19 下午11:40
 * @since 1.8
 */
public class OrgChannelConfigAssembler {

    public static ChannelConfigEntity assembler(ChannelConfigDTO dto) {
        ChannelConfigEntity channelConfigEntity = new ChannelConfigEntity();
        channelConfigEntity.setChannelId(dto.getChannelId());
        channelConfigEntity.setConfigName(dto.getConfigName());
        channelConfigEntity.setConfigValue(dto.getConfigValue());
        channelConfigEntity.setIsDefault(dto.getIsDefault());
        return channelConfigEntity;

    }

    public static OrgChannelConfigVO assembler(ChannelConfigEntity entity) {
        OrgChannelConfigVO orgChannelConfigVO = new OrgChannelConfigVO();
        orgChannelConfigVO.setId(entity.getId());
        orgChannelConfigVO.setOrgId(entity.getOrgId());
        orgChannelConfigVO.setChannelId(entity.getChannelId());
        orgChannelConfigVO.setConfigName(entity.getConfigName());
        orgChannelConfigVO.setConfigValue(entity.getConfigValue());
        orgChannelConfigVO.setStatus(entity.getStatus());
        orgChannelConfigVO.setIsDefault(entity.getIsDefault());
        orgChannelConfigVO.setCreateTime(entity.getCreateTime());
        orgChannelConfigVO.setUpdateTime(entity.getUpdateTime());
        return orgChannelConfigVO;
    }
}
