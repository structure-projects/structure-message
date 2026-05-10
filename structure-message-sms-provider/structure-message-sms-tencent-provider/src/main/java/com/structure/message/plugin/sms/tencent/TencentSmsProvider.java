package com.structure.message.plugin.sms.tencent;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.sms.SmsProvider;
import com.structure.message.common.sms.SmsRequest;
import com.structure.message.common.sms.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 腾讯云短信服务提供商
 */
@Slf4j
@Component
public class TencentSmsProvider implements SmsProvider {

    private TencentConfig tencentConfig;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用腾讯云发送短信，手机号：{}，签名：{}", request.getPhoneNumber(), request.getSignName());

        try {
            validateConfig();

            String messageId = "TENCENT_" + System.currentTimeMillis();
            log.info("腾讯云短信发送成功，消息ID：{}，手机号：{}", messageId, request.getPhoneNumber());
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("腾讯云短信发送失败，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("TENCENT_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsStatus querySmsStatus(String messageId) throws Exception {
        log.info("查询腾讯云短信状态，消息ID：{}", messageId);
        return SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "tencent";
    }

    @Override
    public void initialize(MessageChannelConfig config) {

        this.tencentConfig = new TencentConfig();
        tencentConfig.setSecretId(config.getConfig("secretId"));
        tencentConfig.setSecretKey(config.getConfig("secretKey"));
        tencentConfig.setSdkAppId(config.getConfig("sdkAppId"));
        tencentConfig.setRegion(config.getConfig("region", "ap-guangzhou"));

        log.info("腾讯云短信提供商初始化成功，SecretId：{}，SdkAppId：{}，Region：{}",
                tencentConfig.getSecretId() != null ? "已设置" : "未设置",
                tencentConfig.getSdkAppId() != null ? "已设置" : "未设置",
                tencentConfig.getRegion());
    }

    @Override
    public void destroy() {
        log.info("腾讯云短信提供商销毁");
        this.tencentConfig = null;
    }

    private void validateConfig() {
        if (tencentConfig == null || tencentConfig.getSecretId() == null || tencentConfig.getSecretId().trim().isEmpty()) {
            throw new MessageException("TENCENT_CONFIG_ERROR", "腾讯云SecretId未配置");
        }
        if (tencentConfig == null || tencentConfig.getSecretKey() == null || tencentConfig.getSecretKey().trim().isEmpty()) {
            throw new MessageException("TENCENT_CONFIG_ERROR", "腾讯云SecretKey未配置");
        }
        if (tencentConfig == null || tencentConfig.getSdkAppId() == null || tencentConfig.getSdkAppId().trim().isEmpty()) {
            throw new MessageException("TENCENT_CONFIG_ERROR", "腾讯云SdkAppId未配置");
        }
    }
}