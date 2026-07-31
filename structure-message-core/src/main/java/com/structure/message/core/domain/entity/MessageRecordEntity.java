package com.structure.message.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息记录实体
 */
@Data
@TableName("message_record")
public class MessageRecordEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 组织ID
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

    // 临时
    @TableField(exist = false)
    private String channelCode;

    /**
     * 接收者
     */
    private String receiver;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 模板参数
     */
    private String params;

    /**
     * 附件主题
     */
    private String subject;

    /**
     * 业务来源
     */
    private String businessSource;

    /**
     * 状态：0-待发送，1-发送中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}