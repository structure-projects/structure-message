package com.structure.message.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncExecutorConfig {

    private final MessageExecutorProperties properties;

    @Bean(name = "messageAsyncExecutor")
    public Executor messageAsyncExecutor() {
        log.info("初始化消息异步发送专用线程池（使用配置）");

        MessageExecutorProperties asyncProps = properties;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(asyncProps.getCorePoolSize());
        executor.setMaxPoolSize(asyncProps.getMaxPoolSize());
        executor.setQueueCapacity(asyncProps.getQueueCapacity());
        executor.setThreadNamePrefix(asyncProps.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(getRejectionPolicy(asyncProps.getRejectionPolicy()));
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(asyncProps.getAwaitTerminationSeconds());

        executor.initialize();

        log.info("消息异步发送线程池初始化完成，核心线程数：{}，最大线程数：{}，队列容量：{}，拒绝策略：{}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(),
                executor.getQueueCapacity(), asyncProps.getRejectionPolicy());

        return executor;
    }

    private RejectedExecutionHandler getRejectionPolicy(String policy) {
        switch (policy.toUpperCase()) {
            case "ABORT":
                return new ThreadPoolExecutor.AbortPolicy();
            case "DISCARD":
                return new ThreadPoolExecutor.DiscardPolicy();
            case "DISCARD_OLDEST":
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            case "CALLER_RUNS":
            default:
                return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }
}
