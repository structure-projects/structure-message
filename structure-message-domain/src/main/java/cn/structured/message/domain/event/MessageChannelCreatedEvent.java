package cn.structured.message.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 消息通道创建事件
 * <p>
 * 当消息通道创建成功后触发此事件。
 *
 * @author chuck
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageChannelCreatedEvent extends DomainEvent {

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道类型
     */
    private String channelType;

    /**
     * 创建消息通道创建事件
     *
     * @param channelId   通道ID
     * @param channelCode 通道编码
     * @param channelName 通道名称
     * @param channelType 通道类型
     * @return 消息通道创建事件
     */
    public static MessageChannelCreatedEvent of(Long channelId, String channelCode, String channelName, String channelType) {
        MessageChannelCreatedEvent event = new MessageChannelCreatedEvent();
        event.setChannelCode(channelCode);
        event.setChannelName(channelName);
        event.setChannelType(channelType);
        return DomainEvent.create(event, "message.channel.created", channelId);
    }
}
