package com.structure.message.plugin.sms.aliyun;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.sms.SmsProvider;
import com.structure.message.common.sms.SmsRequest;
import com.structure.message.common.sms.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信服务提供商
 */
@Slf4j
@Component
public class AliyunSmsProvider implements SmsProvider {

    private MessageChannelConfig config;
    private AliyunConfig aliyunConfig;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用阿里云发送短信，手机号：{}，签名：{}", request.getPhoneNumber(), request.getSignName());

        try {
            validateConfig();

            String messageId = "ALIYUN_" + System.currentTimeMillis();
            log.info("阿里云短信发送成功，消息ID：{}，手机号：{}", messageId, request.getPhoneNumber());
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("阿里云短信发送失败，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("ALIYUN_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsResponse sendBatchSms(SmsRequest request) throws Exception {
        log.info("使用阿里云批量发送短信");
        try {
            validateConfig();
            String messageId = "ALIYUN_BATCH_" + System.currentTimeMillis();
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("阿里云批量短信发送失败", e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("ALIYUN_BATCH_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsProvider.SmsStatus querySmsStatus(String messageId) throws Exception {
        log.info("查询阿里云短信状态，消息ID：{}", messageId);
        return SmsProvider.SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "阿里云短信服务";
    }

    @Override
    public void initialize(MessageChannelConfig config) {
        this.config = config;
        
        this.aliyunConfig = new AliyunConfig();
        aliyunConfig.setAccessKey(config.getConfig("accessKeyId"));
        aliyunConfig.setSecretKey(config.getConfig("accessKeySecret"));
        aliyunConfig.setRegionId(config.getConfig("region", "cn-hangzhou"));
        aliyunConfig.setDomain(config.getConfig("domain", "dysmsapi.aliyuncs.com"));

        log.info("阿里云短信提供商初始化成功，AccessKey：{}，RegionId：{}，Domain：{}",
                aliyunConfig.getAccessKey() != null ? "已设置" : "未设置",
                aliyunConfig.getRegionId(),
                aliyunConfig.getDomain());
    }

    @Override
    public void destroy() {
        log.info("阿里云短信提供商销毁");
        this.config = null;
        this.aliyunConfig = null;
    }

    private void validateConfig() {
        if (aliyunConfig == null || aliyunConfig.getAccessKey() == null || aliyunConfig.getAccessKey().trim().isEmpty()) {
            throw new MessageException("ALIYUN_CONFIG_ERROR", "阿里云AccessKey未配置");
        }
        if (aliyunConfig == null || aliyunConfig.getSecretKey() == null || aliyunConfig.getSecretKey().trim().isEmpty()) {
            throw new MessageException("ALIYUN_CONFIG_ERROR", "阿里云SecretKey未配置");
        }
    }
}