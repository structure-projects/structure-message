package cn.structured.message.application.service.impl;

import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.entity.MessageChannel;
import cn.structured.message.domain.repository.ChannelConfigRepository;
import cn.structured.message.domain.repository.MessageChannelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息通道配置查询服务实现类
 * <p>
 * 提供基于通道编码的配置查询功能，用于消息发送时获取配置信息。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageChannelConfigImpl {

    /**
     * 消息通道仓储
     */
    private final MessageChannelRepository messageChannelRepository;

    /**
     * 通道配置仓储
     */
    private final ChannelConfigRepository channelConfigRepository;

    /**
     * 根据机构ID获取所有启用通道的配置
     * <p>
     * 查询所有启用的消息通道，按通道编码分组返回配置列表。
     * </p>
     *
     * @param orgId 机构ID
     * @return 通道编码到配置列表的映射
     */
    public Map<String, List<ChannelConfig>> getConfigsByOrg(Long orgId) {
        List<MessageChannel> channels = messageChannelRepository.findByStatus(1);
        return channels.stream()
                .collect(Collectors.toMap(
                        MessageChannel::getChannelCode,
                        channel -> channelConfigRepository.findByOrgIdAndChannelId(orgId, channel.getId())
                ));
    }

    /**
     * 根据机构ID和通道编码获取配置列表
     * <p>
     * 根据通道编码查询通道信息，再获取该机构在该通道下的所有配置。
     * </p>
     *
     * @param orgId       机构ID
     * @param channelCode 通道编码
     * @return 通道配置列表
     */
    public List<ChannelConfig> getChannelConfigs(Long orgId, String channelCode) {
        MessageChannel channel = messageChannelRepository.findByChannelCode(channelCode)
                .orElse(null);
        if (channel == null) {
            return List.of();
        }
        return channelConfigRepository.findByOrgIdAndChannelId(orgId, channel.getId());
    }
}