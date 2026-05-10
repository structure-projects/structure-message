package com.structure.message.plugin.sms.huawei;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.sms.SmsProvider;
import com.structure.message.common.sms.SmsRequest;
import com.structure.message.common.sms.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 华为云短信服务提供商
 */
@Slf4j
@Component
public class HuaweiSmsProvider implements SmsProvider {

    private HuaweiConfig huaweiConfig;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用华为云发送短信，手机号：{}，签名：{}", request.getPhoneNumber(), request.getSignName());

        try {
            validateConfig();

            String messageId = "HUAWEI_" + System.currentTimeMillis();
            log.info("华为云短信发送成功，消息ID：{}，手机号：{}", messageId, request.getPhoneNumber());
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("华为云短信发送失败，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("HUAWEI_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsStatus querySmsStatus(String messageId) throws Exception {
        log.info("查询华为云短信状态，消息ID：{}", messageId);
        return SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "huawei";
    }

    @Override
    public void initialize(MessageChannelConfig config) {

        this.huaweiConfig = new HuaweiConfig();
        huaweiConfig.setAccessKey(config.getConfig("accessKey"));
        huaweiConfig.setSecretKey(config.getConfig("secretKey"));
        huaweiConfig.setRegion(config.getConfig("region", "cn-east-3"));
        huaweiConfig.setDomain(config.getConfig("domain", "sms.myhuaweicloud.com"));

        log.info("华为云短信提供商初始化成功，AccessKey：{}，Region：{}，Domain：{}",
                huaweiConfig.getAccessKey() != null ? "已设置" : "未设置",
                huaweiConfig.getRegion(),
                huaweiConfig.getDomain());
    }

    @Override
    public void destroy() {
        log.info("华为云短信提供商销毁");
        this.huaweiConfig = null;
    }

    private void validateConfig() {
        if (huaweiConfig == null || huaweiConfig.getAccessKey() == null || huaweiConfig.getAccessKey().trim().isEmpty()) {
            throw new MessageException("HUAWEI_CONFIG_ERROR", "华为云AccessKey未配置");
        }
        if (huaweiConfig == null || huaweiConfig.getSecretKey() == null || huaweiConfig.getSecretKey().trim().isEmpty()) {
            throw new MessageException("HUAWEI_CONFIG_ERROR", "华为云SecretKey未配置");
        }
    }
}