package cn.structured.message.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_accessory")
public class MessageAccessoryPO {

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("message_id")
    private Long messageId;

    @TableField("resource_type")
    private Integer resourceType;

    @TableField("resource_id")
    private String resourceId;

    @TableField("resource_name")
    private String resourceName;

    @TableField("resource_icon")
    private String resourceIcon;

    @TableField("resource_code")
    private String resourceCode;

    @TableField("resource_desc")
    private String resourceDesc;

    @TableField("amount")
    private Long amount;

    @TableField("state")
    private Integer state;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}