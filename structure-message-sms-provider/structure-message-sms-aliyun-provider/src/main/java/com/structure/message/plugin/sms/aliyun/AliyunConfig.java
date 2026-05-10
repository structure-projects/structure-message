package com.structure.message.plugin.sms.aliyun;

import lombok.Data;

@Data
public class AliyunConfig {
    private String accessKey;
    private String secretKey;
    private String regionId = "cn-hangzhou";
    private String domain = "dysmsapi.aliyuncs.com";
    private String version = "2017-05-25";
}
