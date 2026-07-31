package cn.structured.message.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("channel")
public class MessageChannelPO {

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("channel_code")
    private String channelCode;

    @TableField("channel_name")
    private String channelName;

    @TableField("channel_type")
    private String channelType;

    @TableField("plugin_class")
    private String pluginClass;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}