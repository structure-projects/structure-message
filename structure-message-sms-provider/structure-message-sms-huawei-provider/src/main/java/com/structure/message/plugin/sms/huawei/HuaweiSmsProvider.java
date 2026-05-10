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

    private MessageChannelConfig config;
    private HuaweiConfig huaweiConfig;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用华为云发送短信，手机号：{}，签名：{}", request.getPhoneNumber(), request.getSignName());

        try {
            // 验证配置
            validateConfig();

            // 构建华为云短信请求
            // TODO: 集成华为云短信SDK
            // 这里模拟发送成功
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
    public SmsResponse sendBatchSms(SmsRequest request) throws Exception {
        log.info("使用华为云批量发送短信");
        try {
            validateConfig();
            // TODO: 集成华为云批量短信SDK
            String messageId = "HUAWEI_BATCH_" + System.currentTimeMillis();
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("华为云批量短信发送失败", e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("HUAWEI_BATCH_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsProvider.SmsStatus querySmsStatus(String messageId) throws Exception {
        log.info("查询华为云短信状态，消息ID：{}", messageId);
        // TODO: 集成华为云短信状态查询API
        return SmsProvider.SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "华为云短信服务";
    }

    @Override
    public void initialize(MessageChannelConfig config) {
        this.config = config;

        // 使用独立的配置类
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
        this.config = null;
        this.huaweiConfig = null;
    }

    /**
     * 验证配置完整性
     */
    private void validateConfig() {
        if (huaweiConfig == null || huaweiConfig.getAccessKey() == null || huaweiConfig.getAccessKey().trim().isEmpty()) {
            throw new MessageException("HUAWEI_CONFIG_ERROR", "华为云AccessKey未配置");
        }
        if (huaweiConfig == null || huaweiConfig.getSecretKey() == null || huaweiConfig.getSecretKey().trim().isEmpty()) {
            throw new MessageException("HUAWEI_CONFIG_ERROR", "华为云SecretKey未配置");
        }
    }
}
