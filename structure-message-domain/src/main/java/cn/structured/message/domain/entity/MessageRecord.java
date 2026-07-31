package cn.structured.message.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息记录领域实体
 * <p>
 * 表示消息发送的记录，包含发送状态、重试次数、错误信息等。
 * 用于消息追踪和重试机制。
 * </p>
 */
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MessageRecord {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 机构ID，用于多租户隔离
     */
    private Long orgId;

    /**
     * 业务ID，关联业务系统的消息标识
     */
    private String businessId;

    /**
     * 模板ID，关联消息模板
     */
    private Long templateId;

    /**
     * 通道ID，关联消息通道
     */
    private Long channelId;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 接收人（手机号、邮箱、用户ID等）
     */
    private String receiver;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 参数JSON，存储模板参数
     */
    private String params;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 业务来源，标识消息的发起系统
     */
    private String businessSource;

    /**
     * 状态：0-待发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer status;

    /**
     * 错误信息，发送失败时记录
     */
    private String errorMsg;

    /**
     * 实际发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建消息记录
     *
     * @param orgId          机构ID
     * @param businessId     业务ID
     * @param channelCode    通道编码
     * @param receiver       接收人
     * @param content        消息内容
     * @param params         参数JSON
     * @param subject        消息主题
     * @param businessSource 业务来源
     * @return 消息记录实体
     */
    public static MessageRecord create(Long orgId, String businessId, String channelCode, String receiver,
                                       String content, String params, String subject, String businessSource) {
        MessageRecord record = new MessageRecord();
        record.orgId = orgId;
        record.businessId = businessId;
        record.channelCode = channelCode;
        record.receiver = receiver;
        record.content = content;
        record.params = params;
        record.subject = subject;
        record.businessSource = businessSource;
        record.status = 0;
        record.retryTimes = 0;
        return record;
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
     * 设置通道ID
     *
     * @param channelId 通道ID
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    /**
     * 设置模板ID
     *
     * @param templateId 模板ID
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * 标记为待发送状态
     */
    public void markPending() {
        this.status = 0;
    }

    /**
     * 标记为发送中状态
     */
    public void markSending() {
        this.status = 1;
    }

    /**
     * 标记为发送成功状态
     * 设置发送时间为当前时间
     */
    public void markSuccess() {
        this.status = 2;
        this.sendTime = LocalDateTime.now();
    }

    /**
     * 标记为发送失败状态
     *
     * @param errorMsg 错误信息
     */
    public void markFailed(String errorMsg) {
        this.status = 3;
        this.errorMsg = errorMsg;
    }

    /**
     * 增加重试次数
     * 如果重试次数为空，则初始化为0后再加1
     */
    public void incrementRetry() {
        if (this.retryTimes == null) {
            this.retryTimes = 0;
        }
        this.retryTimes++;
    }
}