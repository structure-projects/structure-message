package com.structure.message.plugin.im;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.im.ImProvider;
import com.structure.message.common.im.ImRequest;
import com.structure.message.common.im.ImResponse;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.plugin.api.AbstractMessageChannelPlugin;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * IM消息插件
 */
@Slf4j
@Component
@AllArgsConstructor
public class ImMessagePlugin extends AbstractMessageChannelPlugin {

    private static final String CHANNEL_CODE = "IM";
    private static final String CHANNEL_NAME = "IM消息";

    private final ImPluginConfig imPluginConfig;

    @Autowired
    private ImProviderFactory imProviderFactory;

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
        return ChannelType.FEISHU;
    }

    @Override
    protected void doInitialize(MessageChannelConfig config) throws Exception {
        log.info("初始化IM消息插件");

        imProviderFactory.initializeAllProviders(config);

        String defaultProvider = imPluginConfig.getDefaultProvider();
        ImProvider imProvider = imProviderFactory.getInitializedProvider(defaultProvider);
        if (imProvider == null) {
            throw new MessageException("IM_CONFIG_ERROR", "默认IM服务提供商初始化失败：" + defaultProvider);
        }

        log.info("IM消息插件初始化成功，默认提供商：{}，可用提供商：{}", defaultProvider,
                imProviderFactory.getSupportedProviders().values());
    }

    @Override
    protected MessageResult doSend(MessageContext context) throws Exception {
        log.info("发送IM消息，接收者：{}，内容长度：{}，配置名称：{}", context.getReceiver(),
                context.getContent() != null ? context.getContent().length() : 0, context.getConfigName());

        try {
            String providerName = imPluginConfig.getDefaultProvider();

            if (this.config != null) {
                String configProvider = this.config.getConfig("provider");
                if (configProvider != null && !configProvider.isEmpty()) {
                    providerName = configProvider;
                    log.info("使用配置指定的提供商：{}，配置名称：{}", providerName, context.getConfigName());
                }
            }

            ImProvider imProvider = imProviderFactory.getInitializedProvider(providerName);

            if (imProvider == null) {
                throw new MessageException("IM_PROVIDER_NOT_FOUND", "IM服务提供商未初始化：" + providerName);
            }

            ImRequest request = buildImRequest(context);

            ImResponse response = imProvider.sendImMessage(request);

            if (response.isSuccess()) {
                log.info("IM消息发送成功，接收者：{}，消息ID：{}", context.getReceiver(), response.getMessageId());
                MessageResult result = createSuccessResult(context, response.getMessageId());
                if (response.getRawResponse() != null) {
                    result.setResponseData(response.getRawResponse().toString());
                }
                return result;
            } else {
                log.error("IM消息发送失败，接收者：{}，错误码：{}，错误信息：{}",
                        context.getReceiver(), response.getErrorCode(), response.getErrorMessage());
                MessageResult result = createFailureResult(context, response.getErrorCode(), response.getErrorMessage());
                if (response.getRawResponse() != null) {
                    result.setResponseData(response.getRawResponse().toString());
                }
                return result;
            }

        } catch (Exception e) {
            log.error("IM消息发送失败，接收者：{}", context.getReceiver(), e);
            throw new MessageException("IM_SEND_ERROR", "IM消息发送失败", e);
        }
    }

    @Override
    protected boolean doValidate(MessageContext context) {
        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            log.warn("接收者不能为空");
            return false;
        }

        if (context.getContent() == null || context.getContent().trim().isEmpty()) {
            log.warn("消息内容不能为空");
            return false;
        }

        if (context.getContent().length() > 20000) {
            log.warn("消息内容过长，最大长度20000，当前长度：{}", context.getContent().length());
            return false;
        }

        return true;
    }

    @Override
    protected void doDestroy() throws Exception {
        log.info("销毁IM消息插件");
        if (imProviderFactory != null) {
            imProviderFactory.destroy();
        }
    }

    private ImRequest buildImRequest(MessageContext context) {
        return ImRequest.builder()
                .receiver(context.getReceiver())
                .content(context.getContent())
                .title(context.getSubject())
                .messageType(context.getParams() != null ? (String) context.getParams().get("messageType") : null)
                .url(context.getParams() != null ? (String) context.getParams().get("url") : null)
                .imageUrl(context.getParams() != null ? (String) context.getParams().get("imageUrl") : null)
                .businessId(context.getBusinessId())
                .extra(context.getParams())
                .params(context.getParams())
                .build();
    }
}