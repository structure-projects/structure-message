package cn.structured.message.infra.handler;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.domain.handler.MessageEventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认消息事件处理器实现类
 * <p>
 * 实现MessageEventHandler接口，处理消息发送成功和失败的事件，记录日志。
 * </p>
 */
@Slf4j
public class DefaultMessageEventHandler implements MessageEventHandler {

    /**
     * 消息发送成功事件处理
     * <p>
     * 记录消息发送成功的日志信息。
     * </p>
     *
     * @param context 消息上下文
     * @param result  消息发送结果
     */
    @Override
    public void onMessageSent(MessageContext context, MessageResult result) {
        if (context == null) {
            log.warn("消息发送成功事件处理: context为空");
            return;
        }
        log.info("消息发送成功: channel={}, receiver={}, messageId={}",
                context.getChannelCode(), context.getReceiver(), context.getMessageId());
    }

    /**
     * 消息发送失败事件处理
     * <p>
     * 记录消息发送失败的日志信息，包括错误码和错误信息。
     * </p>
     *
     * @param context   消息上下文
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    @Override
    public void onMessageFailed(MessageContext context, String errorCode, String errorMsg) {
        if (context == null) {
            log.error("消息发送失败事件处理: context为空, errorCode={}, errorMsg={}", errorCode, errorMsg);
            return;
        }
        log.error("消息发送失败: channel={}, receiver={}, messageId={}, errorCode={}, errorMsg={}",
                context.getChannelCode(), context.getReceiver(), context.getMessageId(), errorCode, errorMsg);
    }
}