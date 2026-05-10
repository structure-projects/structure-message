package com.structure.message.plugin.internal.config;

import com.structure.message.plugin.internal.mapper.InternalMessageMapper;
import com.structure.message.plugin.internal.storage.InternalMessageStorage;
import com.structure.message.plugin.internal.storage.MyBatisInternalMessageStorage;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AllArgsConstructor
public class AutoInternalConfiguration {


    private final InternalMessageMapper internalMessageMapper;

    @Bean
    @ConditionalOnMissingBean(InternalMessageStorage.class)
    public InternalMessageStorage InternalMessageStorage() {
        return new MyBatisInternalMessageStorage( internalMessageMapper);
    }
}
