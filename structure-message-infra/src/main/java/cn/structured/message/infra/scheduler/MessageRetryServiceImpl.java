package cn.structured.message.infra.scheduler;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.plugin.MessageChannelPlugin;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.handler.MessageEventHandler;
import cn.structured.message.domain.plugin.PluginManager;
import cn.structured.message.domain.repository.MessageRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息重试服务实现类
 * <p>
 * 实现消息重试的业务逻辑。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageRetryServiceImpl implements MessageRetryService {

    private final PluginManager pluginManager;
    
    private final MessageEventHandler messageEventHandler;
    
    private final MessageRecordRepository messageRecordRepository;

    /**
     * 重试消息发送
     * <p>
     * 根据消息记录重新调用插件发送消息。
     * </p>
     *
     * @param record 消息记录实体
     * @return 是否重试成功
     */
    @Override
    @Transactional
    public boolean retryMessage(MessageRecord record) {
        if (record == null) {
            log.warn("重试消息: record为空");
            return false;
        }
        
        log.info("重试消息: recordId={}, channelCode={}, receiver={}", 
                record.getId(), record.getChannelCode(), record.getReceiver());
        
        // 标记为发送中
        record.markSending();
        messageRecordRepository.save(record);
        
        // 获取插件
        MessageChannelPlugin plugin = pluginManager.getPlugin(record.getChannelCode(), record.getOrgId(), null);
        if (plugin == null) {
            log.error("未找到通道插件: {}", record.getChannelCode());
            record.markFailed("未找到通道插件");
            record.incrementRetry();
            messageRecordRepository.save(record);
            return false;
        }
        
        // 构建消息上下文
        MessageContext context = new MessageContext();
        context.setChannelCode(record.getChannelCode());
        context.setReceiver(record.getReceiver());
        context.setContent(record.getContent());
        context.setOrgId(record.getOrgId());
        context.setBusinessSource(record.getBusinessSource());
        
        try {
            var result = plugin.send(context);
            
            if (result.isSuccess()) {
                record.markSuccess();
                messageEventHandler.onMessageSent(context, result);
                log.info("消息重试成功: recordId={}", record.getId());
            } else {
                record.markFailed(result.getErrorMsg());
                record.incrementRetry();
                messageEventHandler.onMessageFailed(context, result.getErrorCode(), result.getErrorMsg());
                log.warn("消息重试失败: recordId={}, error={}", record.getId(), result.getErrorMsg());
            }
            
            messageRecordRepository.save(record);
            return result.isSuccess();
            
        } catch (Exception e) {
            log.error("消息重试异常: recordId={}, error={}", record.getId(), e.getMessage(), e);
            record.markFailed(e.getMessage());
            record.incrementRetry();
            messageEventHandler.onMessageFailed(context, "RETRY_ERROR", e.getMessage());
            messageRecordRepository.save(record);
            return false;
        }
    }
}