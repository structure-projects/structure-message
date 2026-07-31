package cn.structured.message.domain.handler;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;

/**
 * 消息事件处理器接口
 * <p>
 * 定义消息发送成功和失败时的处理逻辑，由基础设施层实现。
 * </p>
 */
public interface MessageEventHandler {

    /**
     * 消息发送成功事件处理
     *
     * @param context 消息上下文
     * @param result  消息发送结果
     */
    void onMessageSent(MessageContext context, MessageResult result);

    /**
     * 消息发送失败事件处理
     *
     * @param context   消息上下文
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    void onMessageFailed(MessageContext context, String errorCode, String errorMsg);
}