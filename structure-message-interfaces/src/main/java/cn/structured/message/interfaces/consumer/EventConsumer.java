package cn.structured.message.interfaces.consumer;

import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.model.MessageContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final MessageService messageService;

    @RabbitListener(queues = "message.send")
    public void handleMessage(MessageContext context) {
        log.info("接收到消息队列消息: channel={}, receiver={}", context.getChannelCode(), context.getReceiver());
        messageService.sendSync(context);
    }
}