package cn.structured.message.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 消息发送事件
 * <p>
 * 当消息发送成功后触发此事件。
 *
 * @author chuck
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSentEvent extends DomainEvent {

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 接收人
     */
    private String recipient;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建消息发送事件
     *
     * @param recordId   消息记录ID
     * @param channelCode 通道编码
     * @param recipient  接收人
     * @param content    消息内容
     * @return 消息发送事件
     */
    public static MessageSentEvent of(Long recordId, String channelCode, String recipient, String content) {
        MessageSentEvent event = new MessageSentEvent();
        event.setChannelCode(channelCode);
        event.setRecipient(recipient);
        event.setContent(content);
        return DomainEvent.create(event, "message.sent", recordId);
    }
}
