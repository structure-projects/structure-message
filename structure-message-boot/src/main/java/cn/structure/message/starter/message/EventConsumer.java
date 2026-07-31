package cn.structure.message.starter.message;

import com.rabbitmq.client.Channel;
import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.model.MessageContext;
import com.structure.message.core.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * 消息事件消费者
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2021/7/5 14:09
 */
@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final MessageService messageService;

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = MessageConstants.EXCHANGE_MESSAGE, type = ExchangeTypes.TOPIC),
            key = MessageConstants.ROUTING_KEY_SEND,
            value = @Queue(value = MessageConstants.QUEUE_SEND))
            , ackMode = "MANUAL"
    )
    public void consumeMessage(@Payload MessageContext messageContext,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("[EventConsumer] 消费消息 - messageId: {}, channel: {}, receiver: {}",
                messageContext.getMessageId(),
                messageContext.getChannelCode(),
                messageContext.getReceiver());
        try {
            messageService.sendMessage(messageContext);
        } catch (Exception e) {
            log.warn("[EventConsumer] 消息处理失败 - messageId: {}, channel: {}, receiver: {}", messageContext.getMessageId(), messageContext.getChannelCode(), messageContext.getReceiver());
        } finally {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                log.error("[EventConsumer] 消息确认失败 - messageId: {}, channel: {}, receiver: {}",
                        messageContext.getMessageId(),
                        messageContext.getChannelCode(),
                        messageContext.getReceiver());
            }
        }


    }

}
