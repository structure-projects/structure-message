package com.structure.message.plugin.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站内消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalMessageDTO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 接收者用户ID
     */
    private String receiver;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 优先级（1-10）
     */
    private Integer priority;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 读取时间
     */
    private LocalDateTime readTime;
}