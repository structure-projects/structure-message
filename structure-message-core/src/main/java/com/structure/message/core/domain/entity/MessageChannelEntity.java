package com.structure.message.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通道实体
 */
@Data
@TableName("channel")
public class MessageChannelEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道类型
     */
    private String channelType;

    /**
     * 插件实现类
     */
    private String pluginClass;

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