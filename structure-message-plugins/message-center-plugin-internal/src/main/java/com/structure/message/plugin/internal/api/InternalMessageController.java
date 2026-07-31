package com.structure.message.plugin.internal.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import com.structure.message.plugin.internal.InternalMessageDTO;
import com.structure.message.plugin.internal.InternalMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息API控制器
 */
@Tag(name = "站内消息管理")
@RestController
@RequestMapping("/api/internal-message")
@RequiredArgsConstructor
public class InternalMessageController {

    private final InternalMessageService internalMessageService;

    /**
     * 获取用户未读消息数量
     */
    @Operation(summary = "获取用户未读消息数量")
    @GetMapping("/unread-count")
    public ResResultVO<Long>getUnreadMessageCount(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "组织ID") @RequestParam Long orgId) {
        return ResultUtilSimpleImpl.success(internalMessageService.getUnreadMessageCount(userId, orgId));
    }

    /**
     * 获取用户消息列表
     */
    @Operation(summary = "获取用户消息列表")
    @GetMapping("/list")
    public ResResultVO<List<InternalMessageDTO>> getUserMessages(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "组织ID") @RequestParam Long orgId,
            @Parameter(description = "是否已读") @RequestParam(required = false) Boolean isRead,
            @Parameter(description = "限制数量") @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ResultUtilSimpleImpl.success(internalMessageService.getUserMessages(userId, orgId, isRead, limit));
    }

    /**
     * 标记消息为已读
     */
    @Operation(summary = "标记消息为已读")
    @PostMapping("/mark-read/{messageId}")
    public void markAsRead(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        internalMessageService.markAsRead(messageId);
    }

    /**
     * 批量标记消息为已读
     */
    @Operation(summary = "批量标记消息为已读")
    @PostMapping("/mark-read-batch")
    public void markAsReadBatch(@RequestBody List<Long> messageIds) {
        internalMessageService.markAsReadBatch(messageIds);
    }

    /**
     * 删除消息
     */
    @Operation(summary = "删除消息")
    @DeleteMapping("/delete/{messageId}")
    public void deleteMessage(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        internalMessageService.deleteMessage(messageId);
    }
}