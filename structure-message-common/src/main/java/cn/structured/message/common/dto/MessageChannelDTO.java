package cn.structured.message.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageChannelDTO {

    @NotBlank(message = "通道编码不能为空")
    private String channelCode;

    @NotBlank(message = "通道名称不能为空")
    private String channelName;

    @NotBlank(message = "通道类型不能为空")
    private String channelType;

    @NotBlank(message = "插件类名不能为空")
    private String pluginClass;

    private Integer status;
}