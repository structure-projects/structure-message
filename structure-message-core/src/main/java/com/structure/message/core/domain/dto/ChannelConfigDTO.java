package com.structure.message.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 组织通道配置DTO
 * @author chuck
 * @version 2024/07/19 下午11:40
 * @since 1.8
 */
@Data
public class ChannelConfigDTO {
    /**
     * 通道ID
     */
    @NotNull(message = "通道ID不能为空")
    private Long channelId;

    /**
     * 配置名称（用于区分同一通道的多个配置）
     */
    private String configName;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 是否默认配置：1-是，0-否
     */
    private Integer isDefault;
}
