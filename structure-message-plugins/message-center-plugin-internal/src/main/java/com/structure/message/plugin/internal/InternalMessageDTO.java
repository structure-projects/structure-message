package com.structure.message.plugin.internal;

import cn.structured.message.common.model.MessageAccessory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "接收人不能为空")
    private String receiver;

    private String subject;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private String channel;

    private Integer state;

    @NotNull(message = "组织ID不能为空")
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