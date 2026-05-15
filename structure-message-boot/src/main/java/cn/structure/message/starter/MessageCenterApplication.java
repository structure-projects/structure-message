package cn.structure.message.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消息中心启动类
 *
 * @author chuck
 */
@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"cn.structure.message", "com.structure.message"})
public class MessageCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCenterApplication.class, args);
    }
}