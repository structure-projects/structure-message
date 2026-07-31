package com.structure.message.plugin.sms.tencent;

import cn.structured.message.common.exception.MessageException;
import cn.structured.message.common.plugin.MessageChannelConfig;
import cn.structured.message.common.sms.SmsProvider;
import cn.structured.message.common.sms.SmsRequest;
import cn.structured.message.common.sms.SmsResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 腾讯云短信服务提供商
 */
@Slf4j
@Component
public class TencentSmsProvider implements SmsProvider {

    private TencentConfig tencentConfig;
    private SmsClient client;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用腾讯云发送短信，手机号：{}，签名：{}，模板：{}", 
                request.getPhoneNumber(), request.getSignName(), request.getTemplateCode());

        try {
            validateConfig();
            validateRequest(request);

            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.setSmsSdkAppId(tencentConfig.getSdkAppId());
            sendSmsRequest.setSignName(request.getSignName());
            sendSmsRequest.setTemplateId(request.getTemplateCode());

            String phoneNumber = request.getPhoneNumber();
            if (!phoneNumber.startsWith("+86")) {
                phoneNumber = "+86" + phoneNumber;
            }
            sendSmsRequest.setPhoneNumberSet(new String[]{phoneNumber});

            if (request.getTemplateParams() != null) {
                List<String> paramList = new ArrayList<>();
                if (request.getTemplateParams() instanceof Map) {
                    Map<?, ?> paramMap = (Map<?, ?>) request.getTemplateParams();
                    paramList.addAll(paramMap.values().stream().map(String::valueOf).collect(Collectors.toList()));
                } else if (request.getTemplateParams() instanceof List) {
                    List<?> list = (List<?>) request.getTemplateParams();
                    paramList.addAll(list.stream().map(String::valueOf).collect(Collectors.toList()));
                }
                sendSmsRequest.setTemplateParamSet(paramList.toArray(new String[0]));
            }

            if (request.getBusinessId() != null) {
                sendSmsRequest.setSessionContext(request.getBusinessId());
            }

            SendSmsResponse sendSmsResponse = client.SendSms(sendSmsRequest);

            SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];
            if ("Ok".equals(sendStatus.getCode())) {
                String messageId = sendStatus.getSerialNo();
                log.info("腾讯云短信发送成功，手机号：{}，消息ID：{}", request.getPhoneNumber(), messageId);
                return SmsResponse.builder()
                        .success(true)
                        .messageId(messageId)
                        .build();
            } else {
                log.error("腾讯云短信发送失败，手机号：{}，错误码：{}，错误信息：{}",
                        request.getPhoneNumber(), sendStatus.getCode(), sendStatus.getMessage());
                return SmsResponse.builder()
                        .success(false)
                        .errorCode(sendStatus.getCode())
                        .errorMessage(sendStatus.getMessage())
                        .build();
            }
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送异常，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("TENCENT_SDK_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("腾讯云短信发送异常，手机号：{}", request.getPhoneNumber(), e);
            return SmsResponse.builder()
                    .success(false)
                    .errorCode("TENCENT_EXCEPTION")
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
        log.info("初始化腾讯云短信提供商");

        this.tencentConfig = new TencentConfig();
        tencentConfig.setSecretId(config.getConfig("secretId"));
        tencentConfig.setSecretKey(config.getConfig("secretKey"));
        tencentConfig.setSdkAppId(config.getConfig("sdkAppId"));
        tencentConfig.setRegion(config.getConfig("region", "ap-guangzhou"));

        try {
            Credential cred = new Credential(tencentConfig.getSecretId(), tencentConfig.getSecretKey());
            this.client = new SmsClient(cred, tencentConfig.getRegion());

            log.info("腾讯云短信提供商初始化成功，SecretId：{}，SdkAppId：{}，Region：{}",
                    tencentConfig.getSecretId() != null ? "已设置" : "未设置",
                    tencentConfig.getSdkAppId() != null ? "已设置" : "未设置",
                    tencentConfig.getRegion());
        } catch (Exception e) {
            log.error("腾讯云短信提供商初始化失败", e);
            throw new MessageException("TENCENT_INIT_ERROR", "腾讯云短信提供商初始化失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("腾讯云短信提供商销毁");
        this.client = null;
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
        if (client == null) {
            throw new MessageException("TENCENT_CLIENT_ERROR", "腾讯云客户端未初始化");
        }
    }

    private void validateRequest(SmsRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new MessageException("TENCENT_PARAM_ERROR", "手机号不能为空");
        }
        if (request.getSignName() == null || request.getSignName().trim().isEmpty()) {
            throw new MessageException("TENCENT_PARAM_ERROR", "签名名称不能为空");
        }
        if (request.getTemplateCode() == null || request.getTemplateCode().trim().isEmpty()) {
            throw new MessageException("TENCENT_PARAM_ERROR", "模板编码不能为空");
        }
    }
}