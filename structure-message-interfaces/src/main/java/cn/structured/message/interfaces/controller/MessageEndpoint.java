package cn.structured.message.interfaces.controller;

import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageEndpoint {

    private final MessageService messageService;

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
}