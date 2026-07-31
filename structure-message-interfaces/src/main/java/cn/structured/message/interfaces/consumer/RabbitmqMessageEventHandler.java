package cn.structured.message.interfaces.consumer;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.domain.handler.MessageEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqMessageEventHandler implements MessageEventHandler {

    private final AmqpTemplate amqpTemplate;

    public RabbitmqMessageEventHandler(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void onMessageSent(MessageContext context, MessageResult result) {
        log.info("消息发送成功，触发事件: channel={}, messageId={}", context.getChannelCode(), context.getMessageId());
    }

    @Override
    public void onMessageFailed(MessageContext context, String errorCode, String errorMsg) {
        log.error("消息发送失败，触发事件: channel={}, messageId={}, errorCode={}, errorMsg={}",
                context.getChannelCode(), context.getMessageId(), errorCode, errorMsg);
    }
}