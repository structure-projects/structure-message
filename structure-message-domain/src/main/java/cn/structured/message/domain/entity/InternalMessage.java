package cn.structured.message.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内部消息领域实体
 * <p>
 * 表示系统内部消息，用于系统内通知和消息推送。
 * </p>
 */
@Data
@NoArgsConstructor
public class InternalMessage {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 消息类型：0-通知，1-公告，2-私信等
     */
    private Integer type;

    /**
     * 发送人ID
     */
    private String sender;

    /**
     * 接收人ID
     */
    private String receiver;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 通道，标识消息的发送渠道
     */
    private String channel;

    /**
     * 状态：0-未读，1-已读
     */
    private Integer state;

    /**
     * 机构ID，用于多租户隔离
     */
    private Long orgId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 是否删除：false-未删除，true-已删除
     */
    private Boolean deleted;

    /**
     * 创建内部消息
     *
     * @param type     消息类型
     * @param sender   发送人ID
     * @param receiver 接收人ID
     * @param subject  消息主题
     * @param content  消息内容
     * @param channel  通道
     * @param orgId    机构ID
     * @return 内部消息实体
     */
    public static InternalMessage create(Integer type, String sender, String receiver, String subject,
                                         String content, String channel, Long orgId) {
        InternalMessage message = new InternalMessage();
        message.type = type;
        message.sender = sender;
        message.receiver = receiver;
        message.subject = subject;
        message.content = content;
        message.channel = channel;
        message.state = 0;
        message.orgId = orgId;
        message.deleted = false;
        return message;
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
     * 标记为已读
     * 将消息状态设置为已读（1）
     */
    public void markRead() {
        this.state = 1;
    }

    /**
     * 标记为未读
     * 将消息状态设置为未读（0）
     */
    public void markUnread() {
        this.state = 0;
    }
}