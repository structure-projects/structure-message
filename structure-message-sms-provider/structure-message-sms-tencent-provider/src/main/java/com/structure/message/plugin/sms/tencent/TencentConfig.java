package com.structure.message.plugin.sms.tencent;

import cn.structured.message.common.sms.SmsProviderConfig;
import lombok.Data;

@Data
public class TencentConfig extends SmsProviderConfig {

    private String secretId;
    private String secretKey;
    private String sdkAppId;
    private String region = "ap-guangzhou";
}