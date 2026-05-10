package com.structure.message.core.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import cn.structure.starter.tenant.TenantContextHolder;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.core.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Api(tags = "消息管理")
@RestController
@RequestMapping(value = "/api/message")
public class MessageEndpoint {

    @Resource
    private MessageService service;

    @ApiOperation(value = "发送单条消息")
    @PostMapping(value = "/send")
    public ResResultVO<MessageResult> send(@RequestBody @Validated MessageContext context) {
        if (context.getOrgId() == null) {
            try {
                String tenantId = TenantContextHolder.getTenantId();
                if (tenantId != null) {
                    context.setOrgId(Long.parseLong(tenantId));
                }
            } catch (Exception e) {
                context.setOrgId(1L);
            }
        }
        MessageResult messageResult = service.sendMessage(context);
        return ResultUtilSimpleImpl.success(messageResult);
    }

    @ApiOperation(value = "异步发送单条消息")
    @PostMapping(value = "/send/async")
    public ResResultVO<CompletableFuture<MessageResult>> sendAsync(@RequestBody @Validated MessageContext context) {
        if (context.getOrgId() == null) {
            try {
                String tenantId = TenantContextHolder.getTenantId();
                if (tenantId != null) {
                    context.setOrgId(Long.parseLong(tenantId));
                }
            } catch (Exception e) {
                context.setOrgId(1L);
            }
        }
        CompletableFuture<MessageResult> futureResult = service.sendMessageAsync(context);
        return ResultUtilSimpleImpl.success(futureResult);
    }

    @ApiOperation(value = "批量发送消息")
    @PostMapping(value = "/send/batch")
    public ResResultVO<List<MessageResult>> sendBatch(@RequestBody @Validated List<MessageContext> contexts) {
        List<MessageResult> messageResults = service.sendBatchMessages(contexts);
        return ResultUtilSimpleImpl.success(messageResults);
    }

    @ApiOperation(value = "异步批量发送消息")
    @PostMapping(value = "/send/batch/async")
    public ResResultVO<CompletableFuture<List<MessageResult>>> sendBatchAsync(@RequestBody @Validated List<MessageContext> contexts) {
        CompletableFuture<List<MessageResult>> futureResults = service.sendBatchMessagesAsync(contexts);
        return ResultUtilSimpleImpl.success(futureResults);
    }

    @ApiOperation(value = "重新发送消息")
    @PostMapping(value = "/resend/{messageId}")
    public ResResultVO<MessageResult> resend(@ApiParam(value = "消息ID", example = "1")
                                @PathVariable("messageId") Long messageId) {
        MessageResult messageResult = service.resendMessage(messageId);
        return ResultUtilSimpleImpl.success(messageResult);
    }

    @ApiOperation(value = "查询消息发送记录")
    @GetMapping(value = "/records")
    public ResResultVO<List<MessageResult>> queryRecords(@ApiParam(value = "业务ID", example = "ORDER-20240101001") @RequestParam(required = false) String businessId,
                                            @ApiParam(value = "通道编码", example = "INTERNAL") @RequestParam(required = false) String channelCode,
                                            @ApiParam(value = "状态", example = "2", allowableValues = "0,1,2,3") @RequestParam(required = false) Integer status) {
        List<MessageResult> messageResults = service.queryMessageRecords(businessId, channelCode, status);
        return ResultUtilSimpleImpl.success(messageResults);
    }

    @ApiOperation(value = "获取消息发送状态")
    @GetMapping(value = "/status/{messageId}")
    public ResResultVO<MessageResult> getStatus(@ApiParam(value = "消息ID", example = "1")
                                   @PathVariable("messageId") Long messageId) {
        MessageResult messageStatus = service.getMessageStatus(messageId);
        return ResultUtilSimpleImpl.success(messageStatus);
    }
}
