package com.structure.message.plugin.sms.aliyun;

import lombok.Data;

@Data
public class TencentConfig {
    private String secretId;
    private String secretKey;
    private String region = "ap-guangzhou";
    private String sdkAppId;
}
