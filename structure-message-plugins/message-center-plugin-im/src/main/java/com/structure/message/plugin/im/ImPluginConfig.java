package com.structure.message.plugin.im;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IM插件配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "structure.message.im")
public class ImPluginConfig {

    /**
     * 默认服务提供商
     */
    private String defaultProvider = "feishu";

}
