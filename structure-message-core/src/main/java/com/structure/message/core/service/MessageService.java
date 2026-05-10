package com.structure.message.core.service;

import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 发送单条消息
     */
    MessageResult sendMessage(MessageContext context);

    /**
     * 异步发送单条消息
     */
    CompletableFuture<MessageResult> sendMessageAsync(MessageContext context);

    /**
     * 批量发送消息
     */
    List<MessageResult> sendBatchMessages(List<MessageContext> contexts);

    /**
     * 异步批量发送消息
     */
    CompletableFuture<List<MessageResult>> sendBatchMessagesAsync(List<MessageContext> contexts);

    /**
     * 重新发送消息
     */
    MessageResult resendMessage(Long messageId);

    /**
     * 查询消息发送记录
     */
    List<MessageResult> queryMessageRecords(String businessId, String channelCode, Integer status);

    /**
     * 获取消息发送状态
     */
    MessageResult getMessageStatus(Long messageId);
}
