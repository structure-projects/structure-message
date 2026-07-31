package com.structure.message.plugin.internal.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import com.structure.message.plugin.internal.InternalMessageDTO;
import com.structure.message.plugin.internal.InternalMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息API控制器
 */
@Api(tags = "站内消息管理")
@RestController
@RequestMapping("/api/internal-message")
@RequiredArgsConstructor
public class InternalMessageController {

    private final InternalMessageService internalMessageService;

    /**
     * 获取用户未读消息数量
     */
    @ApiOperation("获取用户未读消息数量")
    @GetMapping("/unread-count")
    public ResResultVO<Long>getUnreadMessageCount(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("组织ID") @RequestParam Long orgId) {
        return ResultUtilSimpleImpl.success(internalMessageService.getUnreadMessageCount(userId, orgId));
    }

    /**
     * 获取用户消息列表
     */
    @ApiOperation("获取用户消息列表")
    @GetMapping("/list")
    public ResResultVO<List<InternalMessageDTO>> getUserMessages(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("组织ID") @RequestParam Long orgId,
            @ApiParam("是否已读") @RequestParam(required = false) Boolean isRead,
            @ApiParam("限制数量") @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ResultUtilSimpleImpl.success(internalMessageService.getUserMessages(userId, orgId, isRead, limit));
    }

    /**
     * 标记消息为已读
     */
    @ApiOperation("标记消息为已读")
    @PostMapping("/mark-read/{messageId}")
    public void markAsRead(@ApiParam("消息ID") @PathVariable Long messageId) {
        internalMessageService.markAsRead(messageId);
    }

    /**
     * 批量标记消息为已读
     */
    @ApiOperation("批量标记消息为已读")
    @PostMapping("/mark-read-batch")
    public void markAsReadBatch(@RequestBody List<Long> messageIds) {
        internalMessageService.markAsReadBatch(messageIds);
    }

    /**
     * 删除消息
     */
    @ApiOperation("删除消息")
    @DeleteMapping("/delete/{messageId}")
    public void deleteMessage(@ApiParam("消息ID") @PathVariable Long messageId) {
        internalMessageService.deleteMessage(messageId);
    }
}