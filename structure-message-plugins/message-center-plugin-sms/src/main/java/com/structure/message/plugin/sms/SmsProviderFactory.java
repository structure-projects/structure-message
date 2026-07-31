package com.structure.message.plugin.sms;

import cn.structured.message.common.plugin.MessageChannelConfig;
import cn.structured.message.common.sms.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsProviderFactory {

    private final Map<String, SmsProvider> providers;

    private final Map<String, SmsProvider> initializedProviders = new HashMap<>();


    /**
     * 初始化所有可用的短信提供商
     */
    public void initializeAllProviders(MessageChannelConfig config) {
        for (Map.Entry<String, SmsProvider> entry : providers.entrySet()) {
            SmsProvider provider = entry.getValue();
            String providerName = provider.getProviderName();
            try {
                provider.initialize(config);
                initializedProviders.put(providerName, provider);
                log.info("初始化短信服务提供商成功：{}", providerName);
            } catch (Exception e) {
                log.warn("初始化短信服务提供商失败：{}，该提供商将不可用", providerName, e);
            }
        }
    }

    /**
     * 获取已初始化的提供商，如果未找到则返回 null
     */
    public SmsProvider getInitializedProvider(String providerName) {
        return initializedProviders.get(providerName.toLowerCase());
    }

    public void destroy() {
        for (SmsProvider provider : initializedProviders.values()) {
            try {
                provider.destroy();
            } catch (Exception e) {
                log.error("销毁短信服务提供商失败", e);
            }
        }
        initializedProviders.clear();
    }

    public Map<String, String> getSupportedProviders() {
        Map<String, String> result = new HashMap<>();
        providers.forEach((key, provider) -> result.put(key, provider.getProviderName()));
        return result;
    }

    /**
     * 根据提供者名称获取提供者
     */
    public SmsProvider getProvider(String providerName) {
        return providers.get(providerName.toLowerCase());
    }
}
