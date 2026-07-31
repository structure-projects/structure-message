package com.structure.message.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 消息通道实体
 */
@Data
@ApiModel(description = "消息通道DTO")
public class MessageChannelDTO {

    /**
     * 通道编码
     */
    @ApiModelProperty(value = "通道编码", required = true)
    private String channelCode;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称", required = true)
    private String channelName;

    /**
     * 通道类型
     */
    @ApiModelProperty(value = "通道类型", required = true,notes = "INTERNAL,EMAIL,SMS,IM,MQ,OTHER")
    private String channelType;

    /**
     * 插件实现类
     */
    @ApiModelProperty(value = "插件实现类", required = true)
    private String pluginClass;

    /**
     * 状态：1-启用，0-禁用
     */
    @ApiModelProperty(value = "状态：1-启用，0-禁用", required = true)
    private Integer status;

}