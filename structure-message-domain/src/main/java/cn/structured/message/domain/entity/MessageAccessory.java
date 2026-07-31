package cn.structured.message.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息附件领域实体
 * <p>
 * 表示消息关联的附件资源，如图片、文档等。
 * </p>
 */
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MessageAccessory {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 消息ID，关联消息记录
     */
    private Long messageId;

    /**
     * 资源类型：0-图片，1-文档，2-视频等
     */
    private Integer resourceType;

    /**
     * 资源ID，关联资源服务中的资源标识
     */
    private String resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源图标
     */
    private String resourceIcon;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 资源描述
     */
    private String resourceDesc;

    /**
     * 数量/金额
     */
    private Long amount;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建消息附件
     *
     * @param messageId    消息ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param resourceName 资源名称
     * @return 消息附件实体
     */
    public static MessageAccessory create(Long messageId, Integer resourceType, String resourceId, String resourceName) {
        MessageAccessory accessory = new MessageAccessory();
        accessory.messageId = messageId;
        accessory.resourceType = resourceType;
        accessory.resourceId = resourceId;
        accessory.resourceName = resourceName;
        accessory.state = 1;
        return accessory;
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
     * 激活附件
     * 将附件状态设置为启用状态（1）
     */
    public void activate() {
        this.state = 1;
    }

    /**
     * 停用附件
     * 将附件状态设置为停用状态（0）
     */
    public void deactivate() {
        this.state = 0;
    }
}