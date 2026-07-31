package cn.structured.message.infra.config;

import cn.structured.message.infra.plugin.PluginManagerImpl;
import cn.structured.message.infra.handler.DefaultMessageEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息核心自动配置类
 * <p>
 * 配置消息中心的核心Bean，包括插件管理器和消息事件处理器。
 * </p>
 */
@Configuration
public class MessageCoreAutoConfiguration {

    /**
     * 创建插件管理器Bean
     * <p>
     * 用于管理消息通道插件的注册、获取和注销。
     * </p>
     *
     * @return 插件管理器实现类
     */
    @Bean
    public PluginManagerImpl pluginManager() {
        return new PluginManagerImpl();
    }

    /**
     * 创建消息事件处理器Bean
     * <p>
     * 用于处理消息发送成功和失败的事件。
     * </p>
     *
     * @return 默认消息事件处理器
     */
    @Bean
    public DefaultMessageEventHandler messageEventHandler() {
        return new DefaultMessageEventHandler();
    }
}