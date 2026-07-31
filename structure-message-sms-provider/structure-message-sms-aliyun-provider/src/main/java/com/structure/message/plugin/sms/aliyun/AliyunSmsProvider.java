package com.structure.message.plugin.sms.aliyun;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.alibaba.fastjson.JSON;
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

    private AliyunConfig aliyunConfig;
    private Client client;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用阿里云发送短信，手机号：{}，签名：{}，模板：{}", 
                request.getPhoneNumber(), request.getSignName(), request.getTemplateCode());

        try {
            validateConfig();
            validateRequest(request);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(request.getPhoneNumber())
                    .setSignName(request.getSignName())
                    .setTemplateCode(request.getTemplateCode());

            if (request.getTemplateParams() != null) {
                sendSmsRequest.setTemplateParam(JSON.toJSONString(request.getTemplateParams()));
            }

            if (request.getBusinessId() != null) {
                sendSmsRequest.setOutId(request.getBusinessId());
            }

            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            if ("OK".equals(sendSmsResponse.getBody().getCode())) {
                String messageId = sendSmsResponse.getBody().getBizId();
                log.info("阿里云短信发送成功，手机号：{}，消息ID：{}", request.getPhoneNumber(), messageId);
                return SmsResponse.builder()
                        .success(true)
                        .messageId(messageId)
                        .build();
            } else {
                log.error("阿里云短信发送失败，手机号：{}，错误码：{}，错误信息：{}",
                        request.getPhoneNumber(), sendSmsResponse.getBody().getCode(), 
                        sendSmsResponse.getBody().getMessage());
                return SmsResponse.builder()
                        .success(false)
                        .errorCode(sendSmsResponse.getBody().getCode())
                        .errorMessage(sendSmsResponse.getBody().getMessage())
                        .build();
            }
        } catch (Exception e) {
            log.error("阿里云短信发送异常，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("ALIYUN_EXCEPTION")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsStatus querySmsStatus(String messageId) throws Exception {
        log.info("查询阿里云短信状态，消息ID：{}", messageId);
        return SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "aliyun";
    }

    @Override
    public void initialize(MessageChannelConfig config) {
        log.info("初始化阿里云短信提供商");

        this.aliyunConfig = new AliyunConfig();
        aliyunConfig.setAccessKey(config.getConfig("accessKeyId"));
        aliyunConfig.setSecretKey(config.getConfig("accessKeySecret"));
        aliyunConfig.setRegionId(config.getConfig("region", "cn-hangzhou"));
        aliyunConfig.setDomain(config.getConfig("domain", "dysmsapi.aliyuncs.com"));

        try {
            Config aliConfig = new Config()
                    .setAccessKeyId(aliyunConfig.getAccessKey())
                    .setAccessKeySecret(aliyunConfig.getSecretKey())
                    .setEndpoint(aliyunConfig.getDomain());
            aliConfig.regionId = aliyunConfig.getRegionId();

            this.client = new Client(aliConfig);

            log.info("阿里云短信提供商初始化成功，AccessKey：{}，RegionId：{}，Domain：{}",
                    aliyunConfig.getAccessKey() != null ? "已设置" : "未设置",
                    aliyunConfig.getRegionId(),
                    aliyunConfig.getDomain());
        } catch (Exception e) {
            log.error("阿里云短信提供商初始化失败", e);
            throw new MessageException("ALIYUN_INIT_ERROR", "阿里云短信提供商初始化失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("阿里云短信提供商销毁");
        this.client = null;
        this.aliyunConfig = null;
    }

    private void validateConfig() {
        if (aliyunConfig == null || aliyunConfig.getAccessKey() == null || aliyunConfig.getAccessKey().trim().isEmpty()) {
            throw new MessageException("ALIYUN_CONFIG_ERROR", "阿里云AccessKey未配置");
        }
        if (aliyunConfig == null || aliyunConfig.getSecretKey() == null || aliyunConfig.getSecretKey().trim().isEmpty()) {
            throw new MessageException("ALIYUN_CONFIG_ERROR", "阿里云SecretKey未配置");
        }
        if (client == null) {
            throw new MessageException("ALIYUN_CLIENT_ERROR", "阿里云客户端未初始化");
        }
    }

    private void validateRequest(SmsRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new MessageException("ALIYUN_PARAM_ERROR", "手机号不能为空");
        }
        if (request.getSignName() == null || request.getSignName().trim().isEmpty()) {
            throw new MessageException("ALIYUN_PARAM_ERROR", "签名名称不能为空");
        }
        if (request.getTemplateCode() == null || request.getTemplateCode().trim().isEmpty()) {
            throw new MessageException("ALIYUN_PARAM_ERROR", "模板编码不能为空");
        }
    }
}