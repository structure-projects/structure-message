package cn.structured.message.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ广播配置类
 * <p>
 * 配置消息广播相关的Exchange和Queue，支持消息的广播分发。
 * </p>
 */
@Configuration
public class BroadcastRabbitmqConfiguration {

    /**
     * 广播Exchange名称
     */
    public static final String EXCHANGE_NAME = "message.broadcast";

    /**
     * 广播队列名称
     */
    public static final String QUEUE_NAME = "message.broadcast.queue";

    /**
     * 创建广播Exchange
     * <p>
     * 创建名为"message.broadcast"的Fanout类型Exchange，用于广播消息。
     * </p>
     *
     * @return Fanout类型Exchange
     */
    @Bean
    public FanoutExchange broadcastExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * 创建广播队列
     * <p>
     * 创建名为"message.broadcast.queue"的持久化队列，用于接收广播消息。
     * </p>
     *
     * @return 广播队列
     */
    @Bean
    public Queue broadcastQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * 绑定广播队列到Exchange
     * <p>
     * 将广播队列绑定到广播Exchange，使得队列能够接收广播消息。
     * </p>
     *
     * @param exchange 广播Exchange
     * @param queue    广播队列
     * @return 绑定关系
     */
    @Bean
    public Binding broadcastBinding(FanoutExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange);
    }
}