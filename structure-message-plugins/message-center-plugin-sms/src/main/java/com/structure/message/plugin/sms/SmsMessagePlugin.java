package com.structure.message.plugin.sms;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.plugin.api.AbstractMessageChannelPlugin;
import com.structure.message.common.sms.SmsRequest;
import com.structure.message.common.sms.SmsResponse;
import com.structure.message.common.sms.SmsProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信消息插件
 */
@Slf4j
@Component
@AllArgsConstructor
public class SmsMessagePlugin extends AbstractMessageChannelPlugin {

    private static final String CHANNEL_CODE = "SMS";
    private static final String CHANNEL_NAME = "短信消息";

    private final SmsPluginConfig smsPluginConfig;

    @Autowired
    private SmsProviderFactory smsProviderFactory;

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
        return ChannelType.SMS;
    }

    @Override
    protected void doInitialize(MessageChannelConfig config) throws Exception {
        log.info("初始化短信消息插件");

        // 支持多种配置格式
        String accessKey = config.getConfig("accessKey");
        if (accessKey == null) {
            accessKey = config.getConfig("accessKeyId");
        }
        String secretKey = config.getConfig("secretKey");
        if (secretKey == null) {
            secretKey = config.getConfig("accessKeySecret");
        }
        String secretId = config.getConfig("secretId");
        String signName = config.getConfig("signName");
        String defaultProviderKey = smsPluginConfig.getDefaultProvider();
        String defaultProvider = config.getConfig("provider", defaultProviderKey);

        // 初始化所有可用的短信提供商
        smsProviderFactory.initializeAllProviders(config);

        // 设置默认提供商
        SmsProvider smsProvider = smsProviderFactory.getInitializedProvider(defaultProvider);
        if (smsProvider == null) {
            throw new MessageException("SMS_CONFIG_ERROR", "默认短信服务提供商初始化失败：" + defaultProvider);
        }

        log.info("短信消息插件初始化成功，默认提供商：{}，可用提供商：{}", defaultProvider, 
                smsProviderFactory.getSupportedProviders().keySet());
    }

    @Override
    protected MessageResult doSend(MessageContext context) throws Exception {
        log.info("发送短信消息，接收者：{}，内容长度：{}", context.getReceiver(),
                context.getContent() != null ? context.getContent().length() : 0);

        try {
            // 优先从上下文中获取短信服务提供商
            SmsProvider smsProvider = null;
            String providerFromContext = context.getProvider();
            
            if (providerFromContext != null && !providerFromContext.trim().isEmpty()) {
                smsProvider = smsProviderFactory.getInitializedProvider(providerFromContext);
                if (smsProvider == null) {
                    log.warn("短信服务提供商未初始化：{}", providerFromContext);
                }
            }else {
                smsProvider = smsProviderFactory.getInitializedProvider(smsPluginConfig.getDefaultProvider());
            }

            if (smsProvider == null) {
                throw new MessageException("SMS_PROVIDER_NOT_FOUND", "短信服务提供商未初始化");
            }

            // 构建短信内容
            SmsRequest request = buildSmsRequest(context);

            // 发送短信
            SmsResponse response = smsProvider.sendSms(request);

            if (response.isSuccess()) {
                log.info("短信发送成功，接收者：{}，消息ID：{}", context.getReceiver(), response.getMessageId());
                return createSuccessResult(context, response.getMessageId());
            } else {
                log.error("短信发送失败，接收者：{}，错误码：{}，错误信息：{}",
                        context.getReceiver(), response.getErrorCode(), response.getErrorMessage());
                return createFailureResult(context, response.getErrorCode(), response.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("短信发送失败，接收者：{}", context.getReceiver(), e);
            throw new MessageException("SMS_SEND_ERROR", "短信发送失败", e);
        }
    }

    @Override
    protected boolean doValidate(MessageContext context) {
        // 验证接收者格式（手机号）
        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            log.warn("接收者不能为空");
            return false;
        }

        // 验证手机号格式（简单的中国手机号验证）
        String phoneNumber = context.getReceiver().trim();
        if (!phoneNumber.matches("^1[3-9]\\d{9}$")) {
            log.warn("手机号格式不正确：{}", phoneNumber);
            return false;
        }

        // 验证内容长度
        if (context.getContent() == null || context.getContent().trim().isEmpty()) {
            log.warn("消息内容不能为空");
            return false;
        }

        // 短信内容长度限制（通常70字一条，长短信按67字分割）
        int contentLength = context.getContent().length();
        if (contentLength > 500) { // 最大支持约7条短信
            log.warn("消息内容过长，最大长度500字，当前长度：{}", contentLength);
            return false;
        }

        // 验证模板参数（如果使用模板）
        if (context.getTemplateCode() != null) {
            if (context.getParams() == null || context.getParams().isEmpty()) {
                log.warn("模板消息需要参数");
                return false;
            }
        }

        return true;
    }

    @Override
    protected void doDestroy() throws Exception {
        log.info("销毁短信消息插件");
        // 清理资源
        if (smsProviderFactory != null) {
            smsProviderFactory.destroy();
        }
    }

    /**
     * 构建短信请求
     */
    private SmsRequest buildSmsRequest(MessageContext context) {
        SmsRequest request = new SmsRequest();
        request.setPhoneNumber(context.getReceiver());
        request.setContent(context.getContent());
        request.setTemplateCode(context.getTemplateCode());
        request.setTemplateParams(context.getParams());
        request.setBusinessId(context.getBusinessId());

        // 获取签名名称（从配置中）
        String signName = config.getConfig("signName");
        request.setSignName(signName);

        return request;
    }
}
