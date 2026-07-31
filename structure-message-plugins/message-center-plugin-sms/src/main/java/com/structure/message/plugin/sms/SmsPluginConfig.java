package com.structure.message.plugin.sms;

import cn.structured.message.common.sms.SmsProviderConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信插件配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "message.plugin.sms")
public class SmsPluginConfig {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 默认短信服务提供商（aliyun, tencent, huawei）
     */
    private String defaultProvider = "aliyun";

    /**
     * 默认签名名称
     */
    private String defaultSignName = "消息中心";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 10000;

    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 3;

    /**
     * 重试间隔时间（毫秒）
     */
    private int retryInterval = 1000;

    /**
     * 是否启用发送频率限制
     */
    private boolean rateLimitEnabled = true;

    /**
     * 发送频率限制（条/分钟）
     */
    private int rateLimitPerMinute = 10;

    /**
     * 单条短信最大长度
     */
    private int maxSingleSmsLength = 70;

    /**
     * 长短信最大条数
     */
    private int maxLongSmsCount = 7;


    /**
     * 短信服务提供商配置
     */
    private Map<String, SmsProviderConfig> providers = new HashMap<>();

}