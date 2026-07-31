package cn.structured.message.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * <p>
 * 配置RabbitMQ连接工厂、消息模板和消息队列，用于异步消息发送。
 * </p>
 */
@Slf4j
@Configuration
public class RabbitmqConfiguration {

    /**
     * RabbitMQ服务器地址，默认localhost
     */
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    /**
     * RabbitMQ端口，默认5672
     */
    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    /**
     * RabbitMQ用户名，默认guest
     */
    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    /**
     * RabbitMQ密码，默认guest
     */
    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    /**
     * 创建RabbitMQ连接工厂
     * <p>
     * 配置连接参数，包括主机地址、端口、用户名和密码。
     * </p>
     *
     * @return 连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    /**
     * 创建RabbitMQ消息模板
     * <p>
     * 配置消息转换器为Jackson2JsonMessageConverter，实现JSON序列化。
     * </p>
     *
     * @param connectionFactory 连接工厂
     * @return RabbitMQ消息模板
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    /**
     * 创建消息发送队列
     * <p>
     * 创建名为"message.send"的持久化队列，用于存储待发送的消息。
     * </p>
     *
     * @return 消息队列
     */
    @Bean
    public Queue messageQueue() {
        return new Queue("message.send", true);
    }
}