package com.structure.message.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelPlugin;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.domain.entity.MessageRecordEntity;
import com.structure.message.core.mapper.MessageRecordMapper;
import com.structure.message.core.plugin.PluginManager;
import com.structure.message.core.service.MessageChannelService;
import com.structure.message.core.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final PluginManager pluginManager;
    private final MessageRecordMapper messageRecordMapper;
    private final MessageChannelService messageChannelService;

    @Autowired
    @Qualifier("messageAsyncExecutor")
    private Executor messageAsyncExecutor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sendMessage(MessageContext context) {
        log.info("开始发送消息，通道：{}，接收者：{}",
                context.getChannelCode(), context.getReceiver());

        try {
            validateContext(context);

            if (!pluginManager.isPluginEnabled(context.getChannelCode(), context.getOrgId())) {
                throw new MessageException("CHANNEL_DISABLED", "消息通道已禁用");
            }

            MessageChannelPlugin plugin = pluginManager.getPlugin(context.getChannelCode());
            if (plugin == null) {
                throw new MessageException("PLUGIN_NOT_FOUND", "插件不存在");
            }

            if (!plugin.validate(context)) {
                throw new MessageException("INVALID_MESSAGE", "消息内容验证失败");
            }

            MessageRecordEntity record = saveMessageRecord(context);

            MessageResult result = plugin.send(context);

            updateMessageRecord(record.getId(), result);

            log.info("消息发送完成，消息ID：{}，结果：{}", record.getId(), result.isSuccess());
            return result;

        } catch (Exception e) {
            log.error("消息发送失败，通道：{}，接收者：{}",
                    context.getChannelCode(), context.getReceiver(), e);

            if (e instanceof MessageException) {
                MessageException me = (MessageException) e;
                saveFailedMessageRecord(context, me.getErrorCode(), me.getMessage());
            } else {
                saveFailedMessageRecord(context, "SYSTEM_ERROR", e.getMessage());
            }

            throw e;
        }
    }

    @Override
    public CompletableFuture<MessageResult> sendMessageAsync(MessageContext context) {
        log.info("异步发送消息，通道：{}，接收者：{}，线程池：messageAsyncExecutor",
                context.getChannelCode(), context.getReceiver());

        CompletableFuture<MessageResult> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                MessageResult result = sendMessage(context);
                future.complete(result);
            } catch (Exception e) {
                log.error("异步发送消息失败，通道：{}，接收者：{}",
                        context.getChannelCode(), context.getReceiver(), e);
                future.complete(MessageResult.failure(
                    context.getChannelCode(),
                    context.getReceiver(),
                    "ASYNC_SEND_ERROR",
                    e.getMessage()
                ));
            }
        }, messageAsyncExecutor);

        return future;
    }

    @Override
    public List<MessageResult> sendBatchMessages(List<MessageContext> contexts) {
        log.info("开始批量发送消息，数量：{}", contexts.size());

        List<MessageResult> results = new ArrayList<>();

        for (MessageContext context : contexts) {
            try {
                MessageResult result = sendMessage(context);
                results.add(result);
            } catch (Exception e) {
                log.error("批量发送消息失败，接收者：{}", context.getReceiver(), e);
                results.add(MessageResult.failure(
                    context.getChannelCode(),
                    context.getReceiver(),
                    "BATCH_SEND_ERROR",
                    e.getMessage()
                ));
            }
        }

        log.info("批量发送消息完成，成功：{}，失败：{}",
                results.stream().filter(MessageResult::isSuccess).count(),
                results.stream().filter(r -> !r.isSuccess()).count());

        return results;
    }

    @Override
    public CompletableFuture<List<MessageResult>> sendBatchMessagesAsync(List<MessageContext> contexts) {
        log.info("异步批量发送消息，数量：{}，线程池：messageAsyncExecutor", contexts.size());

        CompletableFuture<List<MessageResult>> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                List<MessageResult> results = sendBatchMessages(contexts);
                future.complete(results);
            } catch (Exception e) {
                log.error("异步批量发送消息失败", e);
                future.complete(
                    contexts.stream()
                        .map(ctx -> MessageResult.failure(
                            ctx.getChannelCode(),
                            ctx.getReceiver(),
                            "ASYNC_BATCH_SEND_ERROR",
                            e.getMessage()
                        ))
                        .collect(Collectors.toList())
                );
            }
        }, messageAsyncExecutor);

        return future;
    }

    @Override
    public MessageResult resendMessage(Long messageId) {
        log.info("重新发送消息，消息ID：{}", messageId);

        MessageRecordEntity record = messageRecordMapper.selectById(messageId);
        if (record == null) {
            throw new MessageException("MESSAGE_NOT_FOUND", "消息记录不存在");
        }

        if (MessageConstants.MessageStatus.SUCCESS == record.getStatus()) {
            throw new MessageException("MESSAGE_ALREADY_SENT", "消息已发送成功，无需重发");
        }

        MessageContext context = MessageContext.builder()
                .orgId(record.getOrgId())
                .businessId(record.getBusinessId())
                .channelCode(getChannelCodeById(record.getChannelId()))
                .receiver(record.getReceiver())
                .content(record.getContent())
                .retryTimes(record.getRetryTimes() != null ? record.getRetryTimes() : 0)
                .maxRetryTimes(MessageConstants.DefaultConfig.MAX_RETRY_TIMES)
                .build();

        return sendMessage(context);
    }

    @Override
    public List<MessageResult> queryMessageRecords(String businessId, String channelCode, Integer status) {
        LambdaQueryWrapper<MessageRecordEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(businessId != null, MessageRecordEntity::getBusinessId, businessId)
               .eq(status != null, MessageRecordEntity::getStatus, status)
               .orderByDesc(MessageRecordEntity::getCreateTime);

        List<MessageRecordEntity> records = messageRecordMapper.selectList(wrapper);
        
        // 如果指定了channelCode，需要先获取对应的channelId，再过滤结果
        if (channelCode != null) {
            MessageChannelEntity channel = messageChannelService.lambdaQuery()
                    .eq(MessageChannelEntity::getChannelCode, channelCode)
                    .one();
            if (channel != null) {
                records = records.stream()
                        .filter(r -> channel.getId().equals(r.getChannelId()))
                        .collect(Collectors.toList());
            } else {
                records = new ArrayList<>();
            }
        }

        return records.stream()
                .map(record -> {
                    // 通过channelId查找对应的channelCode
                    String resolvedChannelCode = null;
                    if (record.getChannelId() != null) {
                        MessageChannelEntity channel = messageChannelService.getById(record.getChannelId());
                        if (channel != null) {
                            resolvedChannelCode = channel.getChannelCode();
                        }
                    }
                    return MessageResult.builder()
                            .success(MessageConstants.MessageStatus.SUCCESS == record.getStatus())
                            .messageId(record.getId())
                            .channelCode(resolvedChannelCode)
                            .receiver(record.getReceiver())
                            .errorMsg(record.getErrorMsg())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public MessageResult getMessageStatus(Long messageId) {
        MessageRecordEntity record = messageRecordMapper.selectById(messageId);
        if (record == null) {
            throw new MessageException("MESSAGE_NOT_FOUND", "消息记录不存在");
        }

        // 通过channelId查找对应的channelCode
        String resolvedChannelCode = null;
        if (record.getChannelId() != null) {
            MessageChannelEntity channel = messageChannelService.getById(record.getChannelId());
            if (channel != null) {
                resolvedChannelCode = channel.getChannelCode();
            }
        }

        return MessageResult.builder()
                .success(MessageConstants.MessageStatus.SUCCESS == record.getStatus())
                .messageId(record.getId())
                .channelCode(resolvedChannelCode)
                .receiver(record.getReceiver())
                .errorMsg(record.getErrorMsg())
                .build();
    }

    private void validateContext(MessageContext context) {
        if (context.getChannelCode() == null || context.getChannelCode().trim().isEmpty()) {
            throw new MessageException("INVALID_PARAM", "通道编码不能为空");
        }
        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            throw new MessageException("INVALID_PARAM", "接收者不能为空");
        }
    }

    private MessageRecordEntity saveMessageRecord(MessageContext context) {
        MessageRecordEntity record = new MessageRecordEntity();
        record.setOrgId(context.getOrgId());
        record.setBusinessId(context.getBusinessId());
        record.setChannelId(getChannelIdByCode(context.getChannelCode()));
        record.setReceiver(context.getReceiver());
        record.setContent(context.getContent());
        record.setParams(context.getParams() != null ? JSON.toJSONString(context.getParams()) : null);
        record.setStatus(MessageConstants.MessageStatus.PENDING);
        record.setRetryTimes(context.getRetryTimes());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        messageRecordMapper.insert(record);
        return record;
    }

    private void saveFailedMessageRecord(MessageContext context, String errorCode, String errorMsg) {
        MessageRecordEntity record = new MessageRecordEntity();
        record.setOrgId(context.getOrgId());
        record.setBusinessId(context.getBusinessId());
        record.setChannelId(getChannelIdByCode(context.getChannelCode()));
        record.setReceiver(context.getReceiver());
        record.setContent(context.getContent());
        record.setParams(context.getParams() != null ? JSON.toJSONString(context.getParams()) : null);
        record.setStatus(MessageConstants.MessageStatus.FAILED);
        record.setErrorMsg(String.format("[%s] %s", errorCode, errorMsg));
        record.setRetryTimes(context.getRetryTimes());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        messageRecordMapper.insert(record);
    }

    private void updateMessageRecord(Long messageId, MessageResult result) {
        MessageRecordEntity record = new MessageRecordEntity();
        record.setId(messageId);
        record.setStatus(result.isSuccess() ? MessageConstants.MessageStatus.SUCCESS : MessageConstants.MessageStatus.FAILED);
        record.setErrorMsg(result.getErrorMsg());
        record.setSendTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        messageRecordMapper.updateById(record);
    }

    private Long getChannelIdByCode(String channelCode) {
        return 1L;
    }

    private String getChannelCodeById(Long channelId) {
        return "INTERNAL";
    }
}
