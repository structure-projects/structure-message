package com.structure.message.core.service.impl;

import cn.structured.mybatis.plus.starter.base.BaseServiceImpl;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.mapper.MessageChannelMapper;
import com.structure.message.core.service.MessageChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageChannelServiceImpl extends BaseServiceImpl<MessageChannelMapper, MessageChannelEntity> implements MessageChannelService {

}
