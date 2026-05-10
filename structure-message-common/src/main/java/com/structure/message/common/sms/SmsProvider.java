package com.structure.message.common.sms;


import com.structure.message.common.plugin.MessageChannelConfig;

/**
 * 短信服务提供商接口
 */
public interface SmsProvider {

    /**
     * 发送短信
     */
    SmsResponse sendSms(SmsRequest request) throws Exception;

    /**
     * 查询短信发送状态
     */
    SmsStatus querySmsStatus(String messageId) throws Exception;

    /**
     * 获取提供商名称
     */
    String getProviderName();

    /**
     * 销毁资源
     */
    void destroy();

    /**
     * 初始化
     */
    void initialize(MessageChannelConfig config);

    /**
     * 短信状态
     */
    enum SmsStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        EXPIRED
    }
}