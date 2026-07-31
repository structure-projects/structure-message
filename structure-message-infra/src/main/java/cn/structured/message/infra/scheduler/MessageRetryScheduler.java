package cn.structured.message.infra.scheduler;

import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.repository.MessageRecordRepository;
import cn.structured.message.infra.scheduler.MessageRetryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息重试调度器
 * <p>
 * 定时扫描待重试的消息，触发重试机制。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageRetryScheduler {

    private final MessageRecordRepository messageRecordRepository;
    
    private final MessageRetryService messageRetryService;

    /**
     * 定时扫描待重试消息
     * <p>
     * 每5分钟扫描一次失败的消息，对未达到最大重试次数的消息进行重试。
     * </p>
     */
    @Scheduled(fixedRate = 300000)
    public void retryFailedMessages() {
        log.info("开始扫描待重试消息");
        
        List<MessageRecord> failedRecords = messageRecordRepository.findByStatus(3);
        
        for (MessageRecord record : failedRecords) {
            if (record.getRetryTimes() == null || record.getRetryTimes() < 3) {
                log.info("重试消息: recordId={}, channelCode={}, retryTimes={}", 
                        record.getId(), record.getChannelCode(), record.getRetryTimes());
                
                try {
                    messageRetryService.retryMessage(record);
                } catch (Exception e) {
                    log.error("消息重试失败: recordId={}, error={}", record.getId(), e.getMessage(), e);
                }
            } else {
                log.info("消息已达最大重试次数，不再重试: recordId={}, retryTimes={}", 
                        record.getId(), record.getRetryTimes());
            }
        }
        
        log.info("待重试消息扫描完成");
    }
}