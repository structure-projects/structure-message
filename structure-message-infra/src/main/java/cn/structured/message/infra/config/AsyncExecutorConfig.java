package cn.structured.message.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务执行器配置类
 * <p>
 * 配置消息中心的异步线程池，用于处理异步消息发送等任务。
 * </p>
 */
@Data
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "message.executor")
public class AsyncExecutorConfig {

    /**
     * 核心线程数，默认5
     */
    private int corePoolSize = 5;

    /**
     * 最大线程数，默认10
     */
    private int maxPoolSize = 10;

    /**
     * 队列容量，默认100
     */
    private int queueCapacity = 100;

    /**
     * 线程名称前缀，默认message-
     */
    private String threadNamePrefix = "message-";

    /**
     * 创建消息异步执行器Bean
     * <p>
     * 配置线程池参数，包括核心线程数、最大线程数、队列容量等。
     * 使用CallerRunsPolicy作为拒绝策略，当线程池满时由调用线程执行任务。
     * </p>
     *
     * @return 线程池执行器
     */
    @Bean("messageAsyncExecutor")
    public Executor messageAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}