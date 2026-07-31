package cn.structured.message.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("org_channel_config")
public class OrgChannelConfigPO {

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("org_id")
    private Long orgId;

    @TableField("channel_id")
    private Long channelId;

    @TableField("config_name")
    private String configName;

    @TableField("config_value")
    private String configValue;

    @TableField("status")
    private Integer status;

    @TableField("is_default")
    private Integer isDefault;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}