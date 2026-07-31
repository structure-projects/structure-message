package cn.structured.message.infra.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * <p>
 * 配置MyBatis的Mapper扫描路径，使得MyBatis能够自动扫描并注册Mapper接口。
 * </p>
 */
@Configuration
@MapperScan("cn.structured.message.repository.mapper")
public class MyBatisConfiguration {
}