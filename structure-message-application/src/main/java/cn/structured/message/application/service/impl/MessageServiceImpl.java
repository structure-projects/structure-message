package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.plugin.MessageChannelPlugin;
import cn.structured.message.domain.handler.MessageEventHandler;
import cn.structured.message.domain.plugin.PluginManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息发送服务实现类
 * <p>
 * 实现MessageService接口，处理消息发送的业务逻辑。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    /**
     * 插件管理器
     */
    private final PluginManager pluginManager;

    /**
     * 消息事件处理器
     */
    private final MessageEventHandler messageEventHandler;

    /**
     * RabbitMQ消息模板
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息（异步）
     * <p>
     * 将消息发送到RabbitMQ队列，由消费者异步处理。
     * </p>
     *
     * @param context 消息上下文
     * @return 消息发送结果
     */
    @Override
    public MessageResult send(MessageContext context) {
        if (context == null) {
            log.warn("消息发送: context为空");
            return MessageResult.failure(null, null, "CONTEXT_NULL", "消息上下文为空");
        }
        
        log.info("异步发送消息到队列: channel={}, receiver={}", context.getChannelCode(), context.getReceiver());
        rabbitTemplate.convertAndSend("message.send", context);
        
        return MessageResult.success(null, context.getChannelCode(), context.getReceiver(), "消息已投递到队列");
    }

    /**
     * 发送消息（同步）
     * <p>
     * 直接调用插件发送消息，等待发送结果返回。
     * </p>
     *
     * @param context 消息上下文
     * @return 消息发送结果
     */
    @Override
    @Transactional
    public MessageResult sendSync(MessageContext context) {
        return sendMessageDirectly(context);
    }

    /**
     * 直接执行消息发送逻辑
     * <p>
     * 获取通道插件，调用插件发送消息，处理发送结果和异常。
     * </p>
     *
     * @param context 消息上下文
     * @return 消息发送结果
     */
    private MessageResult sendMessageDirectly(MessageContext context) {
        if (context == null) {
            log.warn("消息发送: context为空");
            return MessageResult.failure(null, null, "CONTEXT_NULL", "消息上下文为空");
        }
        
        MessageChannelPlugin plugin = pluginManager.getPlugin(context.getChannelCode(), context.getOrgId(), context.getConfigName());
        if (plugin == null) {
            String errorMsg = "未找到通道插件: " + context.getChannelCode();
            log.error(errorMsg);
            messageEventHandler.onMessageFailed(context, "PLUGIN_NOT_FOUND", errorMsg);
            return MessageResult.failure(context.getChannelCode(), context.getReceiver(), "PLUGIN_NOT_FOUND", errorMsg);
        }

        try {
            MessageResult result = plugin.send(context);
            if (result.isSuccess()) {
                messageEventHandler.onMessageSent(context, result);
            } else {
                messageEventHandler.onMessageFailed(context, result.getErrorCode(), result.getErrorMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("消息发送异常: channel={}, receiver={}", context.getChannelCode(), context.getReceiver(), e);
            messageEventHandler.onMessageFailed(context, "SEND_ERROR", e.getMessage());
            return MessageResult.failure(context.getChannelCode(), context.getReceiver(), "SEND_ERROR", e.getMessage());
        }
    }
}