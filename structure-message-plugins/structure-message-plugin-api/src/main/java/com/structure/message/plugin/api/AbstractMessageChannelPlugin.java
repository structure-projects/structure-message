package com.structure.message.plugin.api;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.plugin.MessageChannelPlugin;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息通道插件抽象基类
 */
@Slf4j
public abstract class AbstractMessageChannelPlugin implements MessageChannelPlugin {

    protected MessageChannelConfig config;
    protected volatile boolean initialized = false;
    protected volatile boolean healthy = true;

    @Override
    public void initialize(MessageChannelConfig config) throws MessageException {
        try {
            this.config = config;
            doInitialize(config);
            this.initialized = true;
            this.healthy = true;
            log.info("插件初始化成功，通道编码：{}", getChannelCode());
        } catch (Exception e) {
            this.initialized = false;
            this.healthy = false;
            log.error("插件初始化失败，通道编码：{}", getChannelCode(), e);
            throw new MessageException("PLUGIN_INIT_ERROR", "插件初始化失败", e);
        }
    }

    @Override
    public MessageResult send(MessageContext context) throws MessageException {
        if (!initialized) {
            throw new MessageException("PLUGIN_NOT_INITIALIZED", "插件未初始化");
        }

        if (!healthy) {
            throw new MessageException("PLUGIN_UNHEALTHY", "插件状态异常");
        }

        long startTime = System.currentTimeMillis();
        try {
            log.debug("开始发送消息，通道：{}，接收者：{}", getChannelCode(), context.getReceiver());

            MessageResult result = doSend(context);

            long costTime = System.currentTimeMillis() - startTime;
            result.setCostTime(costTime);

            log.debug("消息发送完成，通道：{}，接收者：{}，结果：{}，耗时：{}ms",
                    getChannelCode(), context.getReceiver(), result.isSuccess(), costTime);

            return result;
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("消息发送失败，通道：{}，接收者：{}，耗时：{}ms",
                    getChannelCode(), context.getReceiver(), costTime, e);

            return MessageResult.failure(
                    getChannelCode(),
                    context.getReceiver(),
                    "SEND_ERROR",
                    e.getMessage()
            );
        }
    }

    @Override
    public List<MessageResult> sendBatch(List<MessageContext> contexts) throws MessageException {
        List<MessageResult> results = new ArrayList<>();

        for (MessageContext context : contexts) {
            try {
                MessageResult result = send(context);
                results.add(result);
            } catch (Exception e) {
                results.add(MessageResult.failure(
                        getChannelCode(),
                        context.getReceiver(),
                        "BATCH_SEND_ERROR",
                        e.getMessage()
                ));
            }
        }

        return results;
    }

    @Override
    public boolean validate(MessageContext context) {
        if (context == null) {
            log.warn("消息上下文为空");
            return false;
        }

        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            log.warn("接收者为空");
            return false;
        }

        return doValidate(context);
    }

    @Override
    public boolean isHealthy() {
        return healthy && initialized;
    }

    @Override
    public void destroy() {
        try {
            doDestroy();
            this.initialized = false;
            this.healthy = false;
            log.info("插件销毁成功，通道编码：{}", getChannelCode());
        } catch (Exception e) {
            log.error("插件销毁失败，通道编码：{}", getChannelCode(), e);
        }
    }

    /**
     * 具体的初始化逻辑，子类实现
     */
    protected abstract void doInitialize(MessageChannelConfig config) throws Exception;

    /**
     * 具体的发送逻辑，子类实现
     */
    protected abstract MessageResult doSend(MessageContext context) throws Exception;

    /**
     * 具体的验证逻辑，子类实现
     */
    protected abstract boolean doValidate(MessageContext context);

    /**
     * 具体的销毁逻辑，子类实现
     */
    protected abstract void doDestroy() throws Exception;

    /**
     * 创建成功的消息结果
     */
    protected MessageResult createSuccessResult(MessageContext context, Object responseData) {
        Long messageId = null;
        if (responseData instanceof Long) {
            messageId = (Long) responseData;
        }
        return MessageResult.builder()
                .success(true)
                .messageId(messageId)
                .channelCode(getChannelCode())
                .receiver(context.getReceiver())
                .responseData(responseData)
                .build();
    }

    /**
     * 创建失败的消息结果
     */
    protected MessageResult createFailureResult(MessageContext context, String errorCode, String errorMsg) {
        return MessageResult.failure(getChannelCode(), context.getReceiver(), errorCode, errorMsg);
    }
}