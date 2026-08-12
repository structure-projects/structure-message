package cn.structured.message.interfaces.controller;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import cn.structure.common.vo.ReqPage;
import cn.structure.common.vo.ResPage;
import cn.structured.message.application.service.MessageRecordService;
import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.dto.MessageRecordQuery;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.vo.MessageRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageEndpoint {

    private final MessageService messageService;
    private final MessageRecordService messageRecordService;

    @PostMapping("/send")
    public MessageResult send(@RequestBody MessageContext context) {
        log.info("接收到消息发送请求: channel={}, receiver={}", context.getChannelCode(), context.getReceiver());
        return messageService.send(context);
    }

    @PostMapping("/send/sync")
    public MessageResult sendSync(@RequestBody MessageContext context) {
        log.info("接收到同步消息发送请求: channel={}, receiver={}", context.getChannelCode(), context.getReceiver());
        return messageService.sendSync(context);
    }

    @GetMapping("/records")
    public ResResultVO<ResPage<MessageRecordVO>> pageRecords(MessageRecordQuery query, ReqPage reqPage) {
        ResPage<MessageRecordVO> result = messageRecordService.page(query, reqPage);
        return ResultUtilSimpleImpl.success(result);
    }

    @GetMapping("/records/{id}")
    public ResResultVO<MessageRecordVO> findById(@PathVariable Long id) {
        MessageRecordVO vo = messageRecordService.findById(id);
        return ResultUtilSimpleImpl.success(vo);
    }

    @PostMapping("/records/{id}/resend")
    public ResResultVO<MessageResult> resend(@PathVariable Long id) {
        log.info("重发消息请求: id={}", id);
        MessageResult result = messageRecordService.resend(id);
        return ResultUtilSimpleImpl.success(result);
    }
}