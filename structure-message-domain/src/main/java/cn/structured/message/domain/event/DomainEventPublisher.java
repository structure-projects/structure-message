package cn.structured.message.domain.event;

/**
 * 领域事件发布器接口
 * <p>
 * 定义领域事件的发布契约，由基础设施层实现。
 *
 * @author chuck
 * @since 1.0.0
 */
public interface DomainEventPublisher {

    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    void publish(DomainEvent event);
}
