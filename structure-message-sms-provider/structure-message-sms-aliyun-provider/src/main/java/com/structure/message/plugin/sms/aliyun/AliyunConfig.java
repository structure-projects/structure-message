package com.structure.message.plugin.sms.aliyun;

import cn.structured.message.common.sms.SmsProviderConfig;
import lombok.Data;

@Data
public class AliyunConfig extends SmsProviderConfig {

    private String accessKey;
    private String secretKey;
    private String regionId = "cn-hangzhou";
    private String domain = "dysmsapi.aliyuncs.com";
}