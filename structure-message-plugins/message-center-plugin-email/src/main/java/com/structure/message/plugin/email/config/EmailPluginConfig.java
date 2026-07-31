package com.structure.message.plugin.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件插件配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "message.plugin.email")
public class EmailPluginConfig {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 默认发件人邮箱
     */
    private String defaultFrom;

    /**
     * 默认发件人名称
     */
    private String defaultFromName = "消息中心";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectionTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int timeout = 10000;

    /**
     * 写入超时时间（毫秒）
     */
    private int writeTimeout = 10000;

    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 3;

    /**
     * 重试间隔时间（毫秒）
     */
    private int retryInterval = 1000;

    /**
     * 是否启用SSL
     */
    private boolean sslEnabled = false;

    /**
     * 是否启用TLS
     */
    private boolean tlsEnabled = true;

    /**
     * 是否启用调试模式
     */
    private boolean debug = false;

    /**
     * 邮件内容最大长度
     */
    private int maxContentLength = 50000;

    /**
     * 附件最大大小（MB）
     */
    private int maxAttachmentSize = 10;

    /**
     * 是否启用模板缓存
     */
    private boolean templateCacheEnabled = true;

    /**
     * 模板缓存大小
     */
    private int templateCacheSize = 100;

    /**
     * 模板路径
     */
    private String templatePath = "classpath:templates/email/";

    /**
     * 是否启用图片内联
     */
    private boolean inlineImagesEnabled = true;

    /**
     * 图片基础URL
     */
    private String imagesBaseUrl;

    /**
     * SMTP配置
     */
    private SmtpConfig smtp = new SmtpConfig();

    /**
     * 模板配置
     */
    private TemplateConfig template = new TemplateConfig();

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

}