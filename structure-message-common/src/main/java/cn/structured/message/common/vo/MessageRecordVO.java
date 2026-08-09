package cn.structured.message.common.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息记录视图对象
 *
 * @author structure-message
 * @version 1.0.0
 */
@Data
public class MessageRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 通道ID
     */
    private Long channelId;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 接收人
     */
    private String receiver;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 参数JSON
     */
    private String params;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 业务来源
     */
    private String businessSource;

    /**
     * 状态：0-待发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer status;

    /**
     * 错误信息
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
}
