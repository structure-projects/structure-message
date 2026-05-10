package cn.structure.message.starter;

import cn.structure.starter.web.restful.annotation.EnableSwagger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消息中心启动类
 *
 * @author chuck
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cn.structure.message", "com.structure.message"})
@MapperScan(basePackages = {"com.structure.message.core.mapper"})
public class MessageCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCenterApplication.class, args);
    }
}