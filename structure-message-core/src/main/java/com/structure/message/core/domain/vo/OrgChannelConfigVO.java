package com.structure.message.core.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组织通道配置VO
 * @author chuck
 * @version 2024/07/19 下午11:40
 * @since 1.8
 */
@Data
public class OrgChannelConfigVO {

    /**
     * 主键ID
     */
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
     * 配置名称
     */
    private String configName;

    /**
     * 配置值
     */
    private String configValue;


    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 是否默认配置：1-是，0-否
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
