package com.structure.message.core.domain.assembler;

import com.structure.message.core.domain.dto.MessageChannelDTO;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.domain.vo.MessageChannelVO;

public class MessageChannelAssembler {

    public static MessageChannelEntity assembler(MessageChannelDTO dto) {
        MessageChannelEntity entity = new MessageChannelEntity();
        entity.setChannelCode(dto.getChannelCode());
        entity.setChannelName(dto.getChannelName());
        entity.setChannelType(dto.getChannelType());
        entity.setPluginClass(dto.getPluginClass());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    public static MessageChannelVO assembler(MessageChannelEntity entity) {
        MessageChannelVO vo = new MessageChannelVO();
        vo.setId(entity.getId());
        vo.setChannelCode(entity.getChannelCode());
        vo.setChannelName(entity.getChannelName());
        vo.setChannelType(entity.getChannelType());
        vo.setPluginClass(entity.getPluginClass());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
