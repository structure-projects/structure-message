package com.structure.message.common.constant;

/**
 * 消息中心常量定义
 */
public class MessageConstants {

    /**
     * 系统常量
     */
    public static final String SYSTEM_NAME = "message-center";
    public static final String SYSTEM_VERSION = "1.0.0";

    /**
     * 消息状态
     */
    public static class MessageStatus {
        public static final int PENDING = 0;    // 待发送
        public static final int SENDING = 1;    // 发送中
        public static final int SUCCESS = 2;    // 发送成功
        public static final int FAILED = 3;     // 发送失败
    }

    /**
     * 通道状态
     */
    public static class ChannelStatus {
        public static final int DISABLED = 0;   // 禁用
        public static final int ENABLED = 1;    // 启用
        public static final int DEFAULT = 1;    // 默认配置标识
    }

    /**
     * 组织状态
     */
    public static class OrganizationStatus {
        public static final int DISABLED = 0;   // 禁用
        public static final int ENABLED = 1;    // 启用
    }

    /**
     * 配置加密状态
     */
    public static class ConfigEncryptStatus {
        public static final int NOT_ENCRYPTED = 0;  // 未加密
        public static final int ENCRYPTED = 1;      // 已加密
    }

    /**
     * 默认配置
     */
    public static class DefaultConfig {
        public static final int MAX_RETRY_TIMES = 3;
        public static final int BATCH_SIZE = 100;
        public static final long TIMEOUT_MILLIS = 30000L;
    }

    /**
     * 缓存键前缀
     */
    public static class CacheKey {
        public static final String CHANNEL_CONFIG = "message:channel:config:";
        public static final String TEMPLATE_CONTENT = "message:template:content:";
        public static final String PLUGIN_STATUS = "message:plugin:status:";
    }

    public static class MessageEvent {
        public static final String MESSAGE_SENT = "message.sent";
        public static final String MESSAGE_FAILED = "message.failed";
        public static final String MESSAGE_RECEIVED = "message.received";
        public static final String MESSAGE_READ = "message.read";
        public static final String MESSAGE_DELETED = "message.deleted";
    }

    // amqp
    // 消息交换机
    public static final String EXCHANGE_MESSAGE = "message.exchange";

    // 发送消息队列
    public static final String QUEUE_SEND = "message.queue.send";

    // 路由键
    public static final String ROUTING_KEY_SEND = "message.routing.send";

    // 广播模式相关常量
    public static final String EXCHANGE_BROADCAST = "message.broadcast.exchange";
    public static final String QUEUE_BROADCAST_PREFIX = "message.queue.broadcast.";
}

