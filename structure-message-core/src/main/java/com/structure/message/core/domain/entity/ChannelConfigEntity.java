package com.structure.message.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组织通道配置实体
 */
@Data
@TableName("org_channel_config")
public class ChannelConfigEntity {

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
     * 通道ID
     */
    private Long channelId;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

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