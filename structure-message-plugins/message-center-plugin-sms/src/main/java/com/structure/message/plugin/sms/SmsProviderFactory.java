package com.structure.message.plugin.sms;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.common.sms.SmsProvider;
import com.structure.message.plugin.sms.aliyun.AliyunSmsProvider;
import com.structure.message.plugin.sms.aliyun.HuaweiSmsProvider;
import com.structure.message.plugin.sms.aliyun.TencentSmsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SmsProviderFactory {

    private final Map<String, SmsProvider> providers = new HashMap<>();
    private final Map<String, SmsProvider> initializedProviders = new HashMap<>();
    private volatile SmsProvider currentProvider;

    @Autowired
    private AliyunSmsProvider aliyunSmsProvider;
    @Autowired(required = false)
    private TencentSmsProvider tencentSmsProvider;
    @Autowired(required = false)
    private HuaweiSmsProvider huaweiSmsProvider;

    @PostConstruct
    public void init() {
        providers.put("aliyun", aliyunSmsProvider);
        
        if (tencentSmsProvider != null) {
            providers.put("tencent", tencentSmsProvider);
        }
        
        if (huaweiSmsProvider != null) {
            providers.put("huawei", huaweiSmsProvider);
        }

        log.info("短信服务提供商工厂初始化完成，已注册提供商：{}", providers.keySet());
    }

    /**
     * 初始化所有可用的短信提供商
     */
    public void initializeAllProviders(MessageChannelConfig config) {
        for (Map.Entry<String, SmsProvider> entry : providers.entrySet()) {
            String providerName = entry.getKey();
            SmsProvider provider = entry.getValue();
            try {
                provider.initialize(config);
                initializedProviders.put(providerName, provider);
                log.info("初始化短信服务提供商成功：{}", providerName);
            } catch (Exception e) {
                log.warn("初始化短信服务提供商失败：{}，该提供商将不可用", providerName, e);
            }
        }
    }

    public SmsProvider createProvider(String providerName, MessageChannelConfig config) {
        SmsProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            throw new MessageException("UNSUPPORTED_SMS_PROVIDER", "不支持的短信服务提供商：" + providerName);
        }

        try {
            provider.initialize(config);
            if (!initializedProviders.containsKey(providerName.toLowerCase())) {
                initializedProviders.put(providerName.toLowerCase(), provider);
            }
            return provider;
        } catch (Exception e) {
            log.error("初始化短信服务提供商失败，提供商：{}", providerName, e);
            throw new MessageException("SMS_PROVIDER_INIT_ERROR", "短信服务提供商初始化失败", e);
        }
    }

    public SmsProvider getCurrentProvider() {
        return currentProvider;
    }

    public void setCurrentProvider(SmsProvider provider) {
        this.currentProvider = provider;
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
