package com.structure.message.core.handler.impl;

import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.core.handler.MessageEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultMessageEventHandler implements MessageEventHandler {

    @Override
    public void onMessageSent(MessageContext context, MessageResult result) {
        log.info("[DefaultHandler] 消息发送成功 - messageId: {}, channel: {}, receiver: {}",
                result.getMessageId(), context.getChannelCode(), context.getReceiver());
    }

    @Override
    public void onMessageFailed(MessageContext context, String errorCode, String errorMsg) {
        log.error("[DefaultHandler] 消息发送失败 - channel: {}, receiver: {}, errorCode: {}, errorMsg: {}",
                context.getChannelCode(), context.getReceiver(), errorCode, errorMsg);
    }
}
