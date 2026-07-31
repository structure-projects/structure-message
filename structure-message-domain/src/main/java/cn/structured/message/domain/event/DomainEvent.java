package cn.structured.message.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 * <p>
 * 所有领域事件的父类，包含事件的基本属性。
 *
 * @author chuck
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 聚合根ID
     */
    private Long aggregateId;

    /**
     * 事件发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 创建领域事件
     *
     * @param eventType   事件类型
     * @param aggregateId 聚合根ID
     * @return 领域事件
     */
    protected static <T extends DomainEvent> T create(T event, String eventType, Long aggregateId) {
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setAggregateId(aggregateId);
        event.setOccurredAt(LocalDateTime.now());
        return event;
    }
}
