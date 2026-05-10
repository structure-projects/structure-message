package com.structure.message.plugin.sms;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.sms.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务提供商工厂
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProviderFactory {

    private final Map<String, SmsProvider> providers = new HashMap<>();
    private volatile SmsProvider currentProvider;

    private final AliyunSmsProvider aliyunSmsProvider;
    private final TencentSmsProvider tencentSmsProvider;
    private final HuaweiSmsProvider huaweiSmsProvider;

    private final SmsProvider defaultProvider;

    @PostConstruct
    public void init() {
        // 注册提供商
        providers.put("aliyun", aliyunSmsProvider);
        providers.put("tencent", tencentSmsProvider);
        providers.put("huawei", huaweiSmsProvider);

        log.info("短信服务提供商工厂初始化完成，已注册提供商：{}", providers.keySet());
    }

    /**
     * 创建短信服务提供商
     */
    public SmsProvider createProvider(String providerName, MessageChannelConfig config) {
        SmsProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            throw new MessageException("UNSUPPORTED_SMS_PROVIDER", "不支持的短信服务提供商：" + providerName);
        }

        try {
            provider.initialize(config);
            return provider;
        } catch (Exception e) {
            log.error("初始化短信服务提供商失败，提供商：{}", providerName, e);
            throw new MessageException("SMS_PROVIDER_INIT_ERROR", "短信服务提供商初始化失败", e);
        }
    }

    /**
     * 获取当前提供商
     */
    public SmsProvider getCurrentProvider() {
        return currentProvider;
    }

    /**
     * 设置当前提供商
     */
    public void setCurrentProvider(SmsProvider provider) {
        this.currentProvider = provider;
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        if (currentProvider != null) {
            try {
                currentProvider.destroy();
            } catch (Exception e) {
                log.error("销毁短信服务提供商失败", e);
            }
        }
    }

    /**
     * 获取所有支持的提供商
     */
    public Map<String, String> getSupportedProviders() {
        Map<String, String> result = new HashMap<>();
        providers.forEach((key, provider) -> result.put(key, provider.getProviderName()));
        return result;
    }
}


