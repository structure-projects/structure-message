package com.structure.message.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "message.executor.async")
public class MessageExecutorProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize = 3;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 8;

    /**
     * 队列容量
     */
    private int queueCapacity = 200;

    /**
     * 线程名前缀
     */
    private String threadNamePrefix = "message-async-";

    /**
     * 等待任务完成的时间（秒）
     */
    private int awaitTerminationSeconds = 60;

    /**
     * 拒绝策略：CALLER_RUNS, ABORT, DISCARD, DISCARD_OLDEST
     */
    private String rejectionPolicy = "CALLER_RUNS";
}
