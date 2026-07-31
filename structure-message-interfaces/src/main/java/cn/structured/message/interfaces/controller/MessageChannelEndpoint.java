package cn.structured.message.interfaces.controller;

import cn.structured.message.application.service.MessageChannelService;
import cn.structured.message.domain.entity.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/channels")
@AllArgsConstructor
public class MessageChannelEndpoint {

    private final MessageChannelService messageChannelService;

    @PostMapping
    public MessageChannel create(@RequestBody MessageChannel channel) {
        log.info("创建消息通道: channelCode={}", channel.getChannelCode());
        return messageChannelService.create(channel);
    }

    @PutMapping("/{id}")
    public MessageChannel update(@PathVariable("id") Long id, @RequestBody MessageChannel channel) {
        log.info("更新消息通道: id={}", id);
        return messageChannelService.update(id, channel);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("删除消息通道: id={}", id);
        messageChannelService.delete(id);
    }

    @GetMapping("/{id}")
    public MessageChannel findById(@PathVariable("id") Long id) {
        return messageChannelService.findById(id);
    }

    @GetMapping("/code/{channelCode}")
    public MessageChannel findByChannelCode(@PathVariable("channelCode") String channelCode) {
        return messageChannelService.findByChannelCode(channelCode);
    }

    @GetMapping
    public List<MessageChannel> findAll() {
        return messageChannelService.findAll();
    }

    @GetMapping("/status/{status}")
    public List<MessageChannel> findByStatus(@PathVariable("status") Integer status) {
        return messageChannelService.findByStatus(status);
    }

    @GetMapping("/type/{channelType}")
    public List<MessageChannel> findByChannelType(@PathVariable("channelType") String channelType) {
        return messageChannelService.findByChannelType(channelType);
    }

    @PostMapping("/{id}/enable")
    public void enable(@PathVariable("id") Long id) {
        log.info("启用消息通道: id={}", id);
        messageChannelService.enable(id);
    }

    @PostMapping("/{id}/disable")
    public void disable(@PathVariable("id") Long id) {
        log.info("禁用消息通道: id={}", id);
        messageChannelService.disable(id);
    }
}