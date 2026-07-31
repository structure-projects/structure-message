package cn.structured.message.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_record")
public class MessageRecordPO {

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("org_id")
    private Long orgId;

    @TableField("business_id")
    private String businessId;

    @TableField("template_id")
    private Long templateId;

    @TableField("channel_id")
    private Long channelId;

    @TableField(exist = false)
    private String channelCode;

    @TableField("receiver")
    private String receiver;

    @TableField("content")
    private String content;

    @TableField("params")
    private String params;

    @TableField("subject")
    private String subject;

    @TableField("business_source")
    private String businessSource;

    @TableField("status")
    private Integer status;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("retry_times")
    private Integer retryTimes;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}