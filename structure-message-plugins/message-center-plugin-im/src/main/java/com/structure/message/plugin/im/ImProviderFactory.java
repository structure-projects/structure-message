package com.structure.message.plugin.im;

import cn.structured.message.common.im.ImProvider;
import cn.structured.message.common.plugin.MessageChannelConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * IM服务提供商工厂
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImProviderFactory {

    private final Map<String, ImProvider> providers;

    private static final Map<String, ImProvider> initializedProviders = new HashMap<>();

    public void initializeAllProviders(MessageChannelConfig config) {
        for (Map.Entry<String, ImProvider> entry : providers.entrySet()) {
            ImProvider provider = entry.getValue();
            String providerName = provider.getProviderName();
            try {
                provider.initialize(config);
                initializedProviders.put(providerName, provider);
                log.info("初始化IM服务提供商成功：{}", providerName);
            } catch (Exception e) {
                log.warn("初始化IM服务提供商失败：{}，该提供商将不可用", providerName, e);
            }
        }
    }

    public ImProvider getInitializedProvider(String providerName) {
        if (providerName == null) {
            return null;
        }
        return initializedProviders.get(providerName.toLowerCase());
    }

    public void destroy() {
        for (ImProvider provider : initializedProviders.values()) {
            try {
                provider.destroy();
            } catch (Exception e) {
                log.error("销毁IM服务提供商失败", e);
            }
        }
        initializedProviders.clear();
    }

    public Map<String, String> getSupportedProviders() {
        Map<String, String> result = new HashMap<>();
        providers.forEach((key, provider) -> result.put(key, provider.getProviderName()));
        return result;
    }

    public ImProvider getProvider(String providerName) {
        return providers.get(providerName.toLowerCase());
    }
}