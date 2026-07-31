package cn.structured.message.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 消息中心配置类
 * <p>
 * 配置消息中心的各项参数，包括重试策略等。
 * </p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "message")
public class MessageConfiguration {

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 重试配置内部类
     */
    @Data
    public static class Retry {
        /**
         * 最大重试次数，默认3次
         */
        private int maxTimes = 3;

        /**
         * 重试间隔时间（毫秒），默认10000ms
         */
        private long intervalMs = 10000;
    }
}