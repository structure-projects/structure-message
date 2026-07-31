package com.structure.message.core.handler;

import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;

public interface MessageEventHandler {

    void onMessageSent(MessageContext context, MessageResult result);

    void onMessageFailed(MessageContext context, String errorCode, String errorMsg);
}
