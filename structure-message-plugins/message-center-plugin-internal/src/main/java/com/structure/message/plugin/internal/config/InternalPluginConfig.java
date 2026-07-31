package com.structure.message.plugin.internal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 站内消息插件配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "message.plugin.internal")
public class InternalPluginConfig {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 最大存储天数
     */
    private int maxStorageDays = 30;

    /**
     * 单用户最大消息数
     */
    private int maxMessagesPerUser = 1000;

    /**
     * 是否启用WebSocket实时通知
     */
    private boolean websocketEnabled = true;

    /**
     * WebSocket端点
     */
    private String websocketEndpoint = "/ws/internal-message";

    /**
     * 消息内容最大长度
     */
    private int maxContentLength = 2000;

    /**
     * 消息标题最大长度
     */
    private int maxTitleLength = 100;

    /**
     * 是否启用消息去重
     */
    private boolean deduplicationEnabled = true;

    /**
     * 去重时间窗口（分钟）
     */
    private int deduplicationWindowMinutes = 5;
}