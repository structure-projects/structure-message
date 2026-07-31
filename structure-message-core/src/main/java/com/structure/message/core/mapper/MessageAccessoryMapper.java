package com.structure.message.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.structure.message.core.domain.entity.MessageAccessoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageAccessoryMapper extends BaseMapper<MessageAccessoryEntity> {

    List<MessageAccessoryEntity> selectByMessageId(Long messageId);
}