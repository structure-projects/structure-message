package com.structure.message.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.structure.message.core.domain.entity.MessageRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息记录Mapper
 */
@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecordEntity> {
}