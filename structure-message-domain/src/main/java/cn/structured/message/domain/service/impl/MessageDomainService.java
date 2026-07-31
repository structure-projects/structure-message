package cn.structured.message.domain.service.impl;

import cn.structure.common.exception.CommonException;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.event.DomainEventPublisher;
import cn.structured.message.domain.event.MessageSentEvent;
import cn.structured.message.domain.repository.MessageRecordRepository;
import cn.structured.message.domain.service.IMessageDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息领域服务实现类
 * <p>
 * 实现消息相关的核心业务逻辑，包括消息发送、通道配置验证、发送失败处理等。
 * </p>
 *
 * @author chuck
 * @since 1.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageDomainService implements IMessageDomainService {

    private final MessageRecordRepository messageRecordRepository;
    
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public MessageRecord createMessageRecord(Long orgId, String businessId, String channelCode, String receiver,
                                             String content, String params, String subject, String businessSource) {
        log.info("创建消息记录: orgId={}, channelCode={}, receiver={}", orgId, channelCode, receiver);

        if (channelCode == null || channelCode.isEmpty()) {
            log.warn("通道编码不能为空");
            throw new CommonException("CHANNEL_CODE_EMPTY", "通道编码不能为空");
        }
        if (receiver == null || receiver.isEmpty()) {
            log.warn("接收人不能为空");
            throw new CommonException("RECEIVER_EMPTY", "接收人不能为空");
        }
        if (content == null || content.isEmpty()) {
            log.warn("消息内容不能为空");
            throw new CommonException("CONTENT_EMPTY", "消息内容不能为空");
        }

        MessageRecord record = MessageRecord.create(orgId, businessId, channelCode, receiver,
                content, params, subject, businessSource);

        record = messageRecordRepository.save(record);
        log.info("消息记录创建成功: recordId={}", record.getId());

        return record;
    }

    @Override
    public void validateChannelConfig(ChannelConfig channelConfig) {
        log.debug("验证通道配置: configId={}", channelConfig.getId());

        if (channelConfig == null) {
            log.warn("通道配置不存在");
            throw new CommonException("CHANNEL_CONFIG_NOT_FOUND", "通道配置不存在");
        }

        if (channelConfig.getStatus() == null || channelConfig.getStatus() != 1) {
            log.warn("通道配置未启用: configId={}", channelConfig.getId());
            throw new CommonException("CHANNEL_CONFIG_DISABLED", "通道配置未启用");
        }

        if (channelConfig.getConfigValue() == null || channelConfig.getConfigValue().isEmpty()) {
            log.warn("通道配置值为空: configId={}", channelConfig.getId());
            throw new CommonException("CHANNEL_CONFIG_VALUE_EMPTY", "通道配置值为空");
        }

        log.debug("通道配置验证通过: configId={}", channelConfig.getId());
    }

    @Override
    public void handleSendFailure(Long messageRecordId, String errorMessage) {
        log.info("处理消息发送失败: recordId={}, error={}", messageRecordId, errorMessage);

        MessageRecord record = messageRecordRepository.findById(messageRecordId);
        if (record == null) {
            log.warn("消息记录不存在: recordId={}", messageRecordId);
            throw new CommonException("MESSAGE_RECORD_NOT_FOUND", "消息记录不存在");
        }

        record.markFailed(errorMessage);
        record.incrementRetry();
        messageRecordRepository.save(record);

        log.info("消息发送失败处理完成: recordId={}, retryTimes={}", record.getId(), record.getRetryTimes());
    }

    @Override
    public void markSendSuccess(Long messageRecordId) {
        log.info("标记消息发送成功: recordId={}", messageRecordId);

        MessageRecord record = messageRecordRepository.findById(messageRecordId);
        if (record == null) {
            log.warn("消息记录不存在: recordId={}", messageRecordId);
            throw new CommonException("MESSAGE_RECORD_NOT_FOUND", "消息记录不存在");
        }

        record.markSuccess();
        messageRecordRepository.save(record);

        log.info("消息发送成功: recordId={}", record.getId());
        
        // 发布消息发送成功领域事件
        MessageSentEvent event = MessageSentEvent.of(record.getId(), record.getChannelCode(), 
                record.getReceiver(), record.getContent());
        domainEventPublisher.publish(event);
    }
}