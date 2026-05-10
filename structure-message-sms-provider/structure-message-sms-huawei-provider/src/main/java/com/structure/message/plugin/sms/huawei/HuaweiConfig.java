package com.structure.message.plugin.sms.huawei;

import com.structure.message.common.sms.SmsProviderConfig;
import lombok.Data;

@Data
public class HuaweiConfig extends SmsProviderConfig {

    private String accessKey;
    private String secretKey;
    private String region = "cn-east-3";
    private String domain = "sms.myhuaweicloud.com";
}