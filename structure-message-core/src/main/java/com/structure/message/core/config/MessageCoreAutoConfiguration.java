package com.structure.message.core.config;

import com.structure.message.core.handler.MessageEventHandler;
import com.structure.message.core.handler.impl.DefaultMessageEventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessageEventHandler.class)
    public MessageEventHandler defaultMessageEventHandler() {
        return new DefaultMessageEventHandler();
    }
}
