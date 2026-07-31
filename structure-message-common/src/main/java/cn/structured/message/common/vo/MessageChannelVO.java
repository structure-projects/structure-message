package cn.structured.message.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageChannelVO {

    private Long id;

    private String channelCode;

    private String channelName;

    private String channelType;

    private String pluginClass;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}