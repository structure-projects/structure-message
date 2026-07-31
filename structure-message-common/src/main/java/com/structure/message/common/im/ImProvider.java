package com.structure.message.common.im;

import com.structure.message.common.plugin.MessageChannelConfig;

/**
 * IM服务提供商接口
 */
public interface ImProvider {

    /**
     * 发送IM消息
     */
    ImResponse sendImMessage(ImRequest request) throws Exception;

    /**
     * 获取提供商名称
     */
    String getProviderName();

    /**
     * 获取渠道编码
     */
    String getChannelCode();

    /**
     * 销毁资源
     */
    void destroy();

    /**
     * 初始化
     */
    void initialize(MessageChannelConfig config);

}
