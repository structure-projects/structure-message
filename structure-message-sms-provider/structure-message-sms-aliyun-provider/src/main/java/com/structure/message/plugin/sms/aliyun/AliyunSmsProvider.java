package com.structure.message.plugin.sms.aliyun;

import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.sms.SmsProvider;
import com.structure.message.common.sms.SmsRequest;
import com.structure.message.common.sms.SmsResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliyunSmsProvider implements SmsProvider {


    private MessageChannelConfig config;

    @Override
    public SmsResponse sendSms(SmsRequest request) throws Exception {
        log.info("使用阿里云发送短信，手机号：{}，签名：{}", request.getPhoneNumber(), request.getSignName());

        try {
            // TODO: 集成阿里云短信SDK
            // 这里模拟发送成功
            String messageId = "ALIYUN_" + System.currentTimeMillis();
            log.info("阿里云短信发送成功，消息ID：{}", messageId);
            return SmsResponse.builder()
                    .success(true)
                    .messageId(messageId)
                    .build();
        } catch (Exception e) {
            log.error("阿里云短信发送失败", e);
            return SmsResponse.builder()
                    .success(true)
                    .errorCode("ALIYUN_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public SmsResponse sendBatchSms(SmsRequest request) throws Exception {
        // TODO: 实现批量发送
        return sendSms(request);
    }

    @Override
    public SmsProvider.SmsStatus querySmsStatus(String messageId) throws Exception {
        // TODO: 实现状态查询
        return SmsProvider.SmsStatus.DELIVERED;
    }

    @Override
    public String getProviderName() {
        return "阿里云短信服务";
    }

    @Override
    public void initialize(MessageChannelConfig config)  {
        this.config = config;
        log.info("阿里云短信提供商初始化成功");
    }

    @Override
    public void destroy() {
        log.info("阿里云短信提供商销毁");
    }
}
