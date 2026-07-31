package com.structure.message.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("internal_message")
public class InternalMessageEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("type")
    private Integer type;

    @TableField("sender")
    private String sender;

    @TableField("receiver")
    private String receiver;

    @TableField("subject")
    private String subject;

    @TableField("content")
    private String content;

    @TableField("channel")
    private String channel;

    @TableField("state")
    private Integer state;

    @TableField("org_id")
    private Long orgId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;
}