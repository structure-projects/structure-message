package com.structure.message.core.mapper;

import cn.structured.mybatis.plus.starter.base.IBaseMapper;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息通道Mapper
 */
@Mapper
public interface MessageChannelMapper extends IBaseMapper<MessageChannelEntity> {
}