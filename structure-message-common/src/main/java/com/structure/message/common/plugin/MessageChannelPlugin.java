package com.structure.message.common.plugin;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;

import java.util.List;

/**
 * 消息通道插件接口
 */
public interface MessageChannelPlugin {

    /**
     * 获取通道编码
     */
    String getChannelCode();

    /**
     * 获取通道名称
     */
    String getChannelName();

    /**
     * 获取通道类型
     */
    ChannelType getChannelType();

    /**
     * 初始化插件
     */
    void initialize(MessageChannelConfig config) throws MessageException;

    /**
     * 发送消息
     */
    MessageResult send(MessageContext context) throws MessageException;

    /**
     * 批量发送消息
     */
    List<MessageResult> sendBatch(List<MessageContext> contexts) throws MessageException;

    /**
     * 验证消息内容
     */
    boolean validate(MessageContext context);

    /**
     * 检查通道状态
     */
    boolean isHealthy();

    /**
     * 销毁插件
     */
    void destroy();

    /**
     * 通道类型枚举
     */
    enum ChannelType {
        INTERNAL("站内消息"),
        SMS("短信消息"),
        EMAIL("邮件消息"),
        WECHAT("微信消息"),
        DINGTALK("钉钉消息");

        private final String description;

        ChannelType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}