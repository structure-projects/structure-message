package cn.structured.message.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息通道领域实体
 * <p>
 * 表示消息中心中用于发送消息的通道，如短信、邮件、IM等。
 * 包含通道的基本信息和业务状态管理方法。
 * </p>
 */
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MessageChannel {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 通道编码，唯一标识一个通道
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道类型，如 SMS、EMAIL、IM
     */
    private String channelType;

    /**
     * 插件类全限定名，用于加载对应的消息发送插件
     */
    private String pluginClass;

    /**
     * 状态
     */
    private Status status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建消息通道
     *
     * @param channelCode 通道编码
     * @param channelName 通道名称
     * @param channelType 通道类型
     * @param pluginClass 插件类全限定名
     * @return 消息通道实体
     */
    public static MessageChannel create(String channelCode, String channelName, String channelType, String pluginClass) {
        MessageChannel channel = new MessageChannel();
        channel.channelCode = channelCode;
        channel.channelName = channelName;
        channel.channelType = channelType;
        channel.pluginClass = pluginClass;
        channel.status = Status.DISABLED;
        return channel;
    }

    /**
     * 设置ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 启用通道
     */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /**
     * 禁用通道
     */
    public void disable() {
        this.status = Status.DISABLED;
    }

    /**
     * 判断通道是否启用
     */
    public boolean isEnabled() {
        return this.status == Status.ENABLED;
    }

    /**
     * 通道状态枚举
     */
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");

        private final int code;
        private final String description;

        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Status of(int code) {
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid channel status: " + code);
        }
    }
}
