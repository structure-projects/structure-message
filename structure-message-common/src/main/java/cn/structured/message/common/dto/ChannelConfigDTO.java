package cn.structured.message.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChannelConfigDTO {

    private Long orgId;

    private Long channelId;

    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @NotBlank(message = "配置值不能为空")
    private String configValue;

    private Integer status;

    private Integer isDefault;
}