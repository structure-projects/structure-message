package cn.structured.message.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通道配置领域实体
 * <p>
 * 表示机构对某个消息通道的配置信息，支持多租户配置隔离。
 * 包含配置的基本信息和业务状态管理方法。
 * </p>
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelConfig {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 机构ID，用于多租户隔离
     */
    private Long orgId;

    /**
     * 通道ID，关联消息通道
     */
    private Long channelId;

    /**
     * 配置名称，用于区分同一通道的不同配置
     */
    private String configName;

    /**
     * 配置值，存储具体的配置内容（JSON格式）
     */
    private String configValue;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 是否默认配置：0-否，1-是
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

    /**
     * 创建通道配置
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @param configValue 配置值
     * @return 通道配置实体
     */
    public static ChannelConfig create(Long orgId, Long channelId, String configName, String configValue) {
        ChannelConfig config = new ChannelConfig();
        config.orgId = orgId;
        config.channelId = channelId;
        config.configName = configName;
        config.configValue = configValue;
        config.status = 0;
        config.isDefault = 0;
        return config;
    }

    /**
     * 设置ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 启用配置
     * 将配置状态设置为启用状态（1）
     */
    public void enable() {
        this.status = 1;
    }

    /**
     * 禁用配置
     * 将配置状态设置为禁用状态（0）
     */
    public void disable() {
        this.status = 0;
    }

    /**
     * 设置为默认配置
     * 将 isDefault 设置为 1
     */
    public void setAsDefault() {
        this.isDefault = 1;
    }

    /**
     * 取消默认配置
     * 将 isDefault 设置为 0
     */
    public void unsetAsDefault() {
        this.isDefault = 0;
    }

    /**
     * 更新配置值
     *
     * @param configValue 配置值
     */
    public void updateConfigValue(String configValue) {
        this.configValue = configValue;
    }
}