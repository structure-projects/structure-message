package cn.structured.message.common.sms;

import lombok.Data;

@Data
public class SmsProviderConfig {

    /**
     * 提供商代码
     */
    private String providerCode;

    /**
     * 提供商名称
     */
    private String name;
}
