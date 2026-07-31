package cn.structured.message.interfaces.controller;

import cn.structured.message.application.service.ChannelConfigService;
import cn.structured.message.domain.entity.ChannelConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel-configs")
@AllArgsConstructor
public class MessageChannelConfigEndpoint {

    private final ChannelConfigService channelConfigService;

    @PostMapping
    public ChannelConfig create(@RequestBody ChannelConfig config) {
        log.info("创建通道配置: orgId={}, channelId={}, configName={}", config.getOrgId(), config.getChannelId(), config.getConfigName());
        return channelConfigService.create(config);
    }

    @PutMapping("/{id}")
    public ChannelConfig update(@PathVariable Long id, @RequestBody ChannelConfig config) {
        log.info("更新通道配置: id={}", id);
        return channelConfigService.update(id, config);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("删除通道配置: id={}", id);
        channelConfigService.delete(id);
    }

    @GetMapping("/{id}")
    public ChannelConfig findById(@PathVariable Long id) {
        return channelConfigService.findById(id);
    }

    @GetMapping("/org/{orgId}")
    public List<ChannelConfig> findByOrgId(@PathVariable Long orgId) {
        return channelConfigService.findByOrgId(orgId);
    }

    @GetMapping("/channel/{channelId}")
    public List<ChannelConfig> findByChannelId(@PathVariable Long channelId) {
        return channelConfigService.findByChannelId(channelId);
    }

    @GetMapping("/org/{orgId}/channel/{channelId}")
    public List<ChannelConfig> findByOrgIdAndChannelId(@PathVariable Long orgId, @PathVariable Long channelId) {
        return channelConfigService.findByOrgIdAndChannelId(orgId, channelId);
    }

    @GetMapping("/org/{orgId}/channel/{channelId}/config/{configName}")
    public ChannelConfig findByOrgIdAndChannelIdAndConfigName(@PathVariable Long orgId, @PathVariable Long channelId, @PathVariable String configName) {
        return channelConfigService.findByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName);
    }

    @GetMapping("/org/{orgId}/channel/{channelId}/default")
    public ChannelConfig findDefaultByOrgIdAndChannelId(@PathVariable Long orgId, @PathVariable Long channelId) {
        return channelConfigService.findDefaultByOrgIdAndChannelId(orgId, channelId);
    }

    @PostMapping("/{id}/enable")
    public void enable(@PathVariable Long id) {
        log.info("启用通道配置: id={}", id);
        channelConfigService.enable(id);
    }

    @PostMapping("/{id}/disable")
    public void disable(@PathVariable Long id) {
        log.info("禁用通道配置: id={}", id);
        channelConfigService.disable(id);
    }

    @PostMapping("/{id}/set-default")
    public void setAsDefault(@PathVariable Long id) {
        log.info("设为默认配置: id={}", id);
        channelConfigService.setAsDefault(id);
    }

    @PostMapping("/{id}/unset-default")
    public void unsetAsDefault(@PathVariable Long id) {
        log.info("取消默认配置: id={}", id);
        channelConfigService.unsetAsDefault(id);
    }
}