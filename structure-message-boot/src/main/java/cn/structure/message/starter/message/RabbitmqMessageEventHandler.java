package cn.structure.message.starter.message;

import com.structure.message.common.constant.MessageConstants;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.core.handler.MessageEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
@RequiredArgsConstructor
public class RabbitmqMessageEventHandler implements MessageEventHandler {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void onMessageSent(MessageContext context, MessageResult result) {
        log.info("[RabbitmqMessageEventHandler] 消息发送成功 - messageId: {}, channel: {}, receiver: {}",
                result.getMessageId(), context.getChannelCode(), context.getReceiver());

        // 发送到广播交换机（新增广播功能）
        rabbitTemplate.convertAndSend(MessageConstants.EXCHANGE_BROADCAST, "", context);

        log.debug("[RabbitmqMessageEventHandler] 消息已同时发送到普通队列和广播交换机");
    }

    @Override
    public void onMessageFailed(MessageContext context, String errorCode, String errorMsg) {
        log.error("[RabbitmqMessageEventHandler] 消息发送失败 - channel: {}, receiver: {}, errorCode: {}, errorMsg: {}",
                context.getChannelCode(), context.getReceiver(), errorCode, errorMsg);

        // 创建失败结果对象
        MessageResult failedResult = MessageResult.failure(
                context.getChannelCode(),
                context.getReceiver(),
                errorCode,
                errorMsg
        );

        // 发送失败事件到广播交换机
        rabbitTemplate.convertAndSend(MessageConstants.EXCHANGE_BROADCAST, "", failedResult);
    }
}
