package cn.structured.message.application.service;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;

/**
 * 消息发送服务接口
 * <p>
 * 定义消息发送的业务操作，包括同步发送和异步发送。
 * </p>
 */
public interface MessageService {

    /**
     * 发送消息（异步）
     *
     * @param context 消息上下文
     * @return 消息发送结果
     */
    MessageResult send(MessageContext context);

    /**
     * 发送消息（同步）
     *
     * @param context 消息上下文
     * @return 消息发送结果
     */
    MessageResult sendSync(MessageContext context);
}