package cn.structured.message.infra.event;

import cn.structured.message.domain.event.DomainEvent;
import cn.structured.message.domain.event.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布器实现类
 * <p>
 * 实现领域事件的发布功能，当前版本先通过日志记录事件，后续可扩展为消息队列或事件总线。
 * </p>
 */
@Slf4j
@Component
public class DomainEventPublisherImpl implements DomainEventPublisher {

    /**
     * 发布领域事件
     * <p>
     * 当前实现：记录事件日志，便于后续追踪和扩展。
     * </p>
     *
     * @param event 领域事件
     */
    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            log.warn("领域事件发布：事件为空");
            return;
        }
        
        log.info("领域事件发布：eventId={}, eventType={}, aggregateId={}, occurredAt={}",
                event.getEventId(), event.getEventType(), 
                event.getAggregateId(), event.getOccurredAt());
        
        // 后续可扩展：发送到消息队列、事件总线等
        // rabbitTemplate.convertAndSend("domain.event", event);
    }
}