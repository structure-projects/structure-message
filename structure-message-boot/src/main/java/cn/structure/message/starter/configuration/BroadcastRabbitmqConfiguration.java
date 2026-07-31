package cn.structure.message.starter.configuration;


import com.structure.message.common.constant.MessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ广播模式配置
 */
@Slf4j
@Configuration
public class BroadcastRabbitmqConfiguration {

    /**
     * 创建广播交换机
     */
    @Bean
    public FanoutExchange broadcastExchange() {
        log.info("创建广播交换机: {}", MessageConstants.EXCHANGE_BROADCAST);
        return new FanoutExchange(MessageConstants.EXCHANGE_BROADCAST, true, false);
    }

    /**
     * 创建广播队列 - 消息发送事件
     */
    @Bean
    public Queue broadcastSendQueue() {
        String queueName = MessageConstants.QUEUE_BROADCAST_PREFIX + "send";
        log.info("创建广播队列: {}", queueName);
        return new Queue(queueName, true, false, false);
    }

    /**
     * 创建广播队列 - 消息失败事件
     */
    @Bean
    public Queue broadcastFailedQueue() {
        String queueName = MessageConstants.QUEUE_BROADCAST_PREFIX + "failed";
        log.info("创建广播队列: {}", queueName);
        return new Queue(queueName, true, false, false);
    }

    /**
     * 绑定发送队列到广播交换机
     */
    @Bean
    public Binding bindBroadcastSendQueue(Queue broadcastSendQueue, FanoutExchange broadcastExchange) {
        log.info("绑定发送队列到广播交换机");
        return BindingBuilder.bind(broadcastSendQueue).to(broadcastExchange);
    }

    /**
     * 绑定失败队列到广播交换机
     */
    @Bean
    public Binding bindBroadcastFailedQueue(Queue broadcastFailedQueue, FanoutExchange broadcastExchange) {
        log.info("绑定失败队列到广播交换机");
        return BindingBuilder.bind(broadcastFailedQueue).to(broadcastExchange);
    }
}