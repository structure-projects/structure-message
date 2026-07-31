package com.structure.message.plugin.internal;

import com.structure.message.common.model.MessageAccessory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalMessageDTO {

    private Long id;

    private Integer type;

    private String sender;

    private String receiver;

    private String subject;

    private String content;

    private String channel;

    private Integer state;

    private Long orgId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;

    private Boolean deleted;

    private String businessId;

    private Integer priority;

    private Boolean isRead;

    private LocalDateTime readTime;

    private List<MessageAccessory> accessories;
}