package com.structure.message.plugin.internal;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.plugin.api.AbstractMessageChannelPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 站内消息插件
 */
@Slf4j
@Component
public class InternalMessagePlugin extends AbstractMessageChannelPlugin {

    private static final String CHANNEL_CODE = "INTERNAL";
    private static final String CHANNEL_NAME = "站内消息";

    @Autowired
    private InternalMessageService internalMessageService;

    @Override
    public String getChannelCode() {
        return CHANNEL_CODE;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.INTERNAL;
    }

    @Override
    protected void doInitialize(MessageChannelConfig config) throws Exception {
        log.info("初始化站内消息插件");

        // 验证必要的配置
        String maxStorageDays = config.getConfig("maxStorageDays", "30");
        String maxMessagesPerUser = config.getConfig("maxMessagesPerUser", "1000");

        log.info("站内消息插件配置 - 最大存储天数：{}，单用户最大消息数：{}", maxStorageDays, maxMessagesPerUser);
    }

    @Override
    protected MessageResult doSend(MessageContext context) throws Exception {
        log.info("发送站内消息，接收者：{}，内容：{}", context.getReceiver(), context.getContent());

        try {
            String title = context.getParams() != null ? (String) context.getParams().get("title") : null;
            
            InternalMessageDTO message = InternalMessageDTO.builder()
                    .id(context.getMessageId())
                    .type(1)
                    .sender(context.getSender())
                    .receiver(context.getReceiver())
                    .subject(title != null ? title : context.getSubject())
                    .content(context.getContent())
                    .channel("1")
                    .state(1)
                    .orgId(context.getOrgId())
                    .businessId(context.getBusinessId())
                    .priority(context.getPriority() != null ? context.getPriority() : 5)
                    .deleted(false)
                    .accessories(context.getAccessories())
                    .build();

            Long messageId = internalMessageService.saveMessage(message);

            log.info("站内消息发送成功，消息ID：{}，接收者：{}", messageId, context.getReceiver());

            return createSuccessResult(context, messageId);

        } catch (Exception e) {
            log.error("站内消息发送失败，接收者：{}", context.getReceiver(), e);
            throw new MessageException("INTERNAL_SEND_ERROR", "站内消息发送失败", e);
        }
    }

    @Override
    protected boolean doValidate(MessageContext context) {
        // 验证接收者格式（假设是用户ID）
        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            log.warn("接收者不能为空");
            return false;
        }

        // 验证内容长度
        if (context.getContent() == null || context.getContent().trim().isEmpty()) {
            log.warn("消息内容不能为空");
            return false;
        }

        if (context.getContent().length() > 2000) {
            log.warn("消息内容过长，最大长度2000，当前长度：{}", context.getContent().length());
            return false;
        }

        // 验证优先级范围
        if (context.getPriority() != null && (context.getPriority() < 1 || context.getPriority() > 10)) {
            log.warn("优先级范围错误，应在1-10之间，当前值：{}", context.getPriority());
            return false;
        }

        return true;
    }

    @Override
    protected void doDestroy() throws Exception {
        log.info("销毁站内消息插件");
        // 清理资源
    }
}