package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.MessageChannelService;
import cn.structured.message.common.exception.MessageException;
import cn.structured.message.domain.entity.MessageChannel;
import cn.structured.message.domain.repository.MessageChannelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息通道服务实现类
 * <p>
 * 实现MessageChannelService接口，处理消息通道的业务逻辑。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageChannelServiceImpl implements MessageChannelService {

    /**
     * 消息通道仓储
     */
    private final MessageChannelRepository messageChannelRepository;

    /**
     * 创建消息通道
     * <p>
     * 验证通道编码唯一性，设置默认状态为启用，保存通道信息。
     * </p>
     *
     * @param channel 消息通道实体
     * @return 创建后的消息通道实体
     */
    @Override
    @Transactional
    public MessageChannel create(MessageChannel channel) {
        if (messageChannelRepository.existsByChannelCode(channel.getChannelCode())) {
            throw new MessageException("CHANNEL_CODE_EXISTS", "通道编码已存在: " + channel.getChannelCode());
        }
        if (channel.getStatus() == null) {
            channel.enable();
        }
        log.info("创建消息通道: channelCode={}, channelName={}", channel.getChannelCode(), channel.getChannelName());
        return messageChannelRepository.save(channel);
    }

    /**
     * 更新消息通道
     * <p>
     * 验证通道存在性和编码唯一性，更新通道信息。
     * </p>
     *
     * @param id      通道ID
     * @param channel 消息通道实体
     * @return 更新后的消息通道实体
     */
    @Override
    @Transactional
    public MessageChannel update(Long id, MessageChannel channel) {
        MessageChannel existing = messageChannelRepository.findById(id);
        if (existing == null) {
            throw new MessageException("CHANNEL_NOT_FOUND", "通道不存在: " + id);
        }

        if (messageChannelRepository.existsByChannelCodeAndNeId(channel.getChannelCode(), id)) {
            throw new MessageException("CHANNEL_CODE_EXISTS", "通道编码已存在: " + channel.getChannelCode());
        }

        if (channel.getStatus() != null) {
            if (channel.getStatus() == MessageChannel.Status.ENABLED) {
                existing.enable();
            } else {
                existing.disable();
            }
        }
        
        log.info("更新消息通道: id={}, channelCode={}", id, channel.getChannelCode());
        return messageChannelRepository.save(channel);
    }

    /**
     * 删除消息通道
     * <p>
     * 验证通道存在性，删除通道信息。
     * </p>
     *
     * @param id 通道ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (messageChannelRepository.findById(id) == null) {
            throw new MessageException("CHANNEL_NOT_FOUND", "通道不存在: " + id);
        }
        log.info("删除消息通道: id={}", id);
        messageChannelRepository.removeById(id);
    }

    /**
     * 根据ID查询消息通道
     *
     * @param id 通道ID
     * @return 消息通道实体
     */
    @Override
    public MessageChannel findById(Long id) {
        MessageChannel channel = messageChannelRepository.findById(id);
        if (channel == null) {
            throw new MessageException("CHANNEL_NOT_FOUND", "通道不存在: " + id);
        }
        return channel;
    }

    /**
     * 根据通道编码查询消息通道
     *
     * @param channelCode 通道编码
     * @return 消息通道实体
     */
    @Override
    public MessageChannel findByChannelCode(String channelCode) {
        return messageChannelRepository.findByChannelCode(channelCode)
                .orElseThrow(() -> new MessageException("CHANNEL_NOT_FOUND", "通道不存在: " + channelCode));
    }

    /**
     * 查询所有消息通道
     *
     * @return 消息通道列表
     */
    @Override
    public List<MessageChannel> findAll() {
        return messageChannelRepository.queryList(null);
    }

    /**
     * 根据状态查询消息通道列表
     *
     * @param status 状态：0-禁用，1-启用
     * @return 消息通道列表
     */
    @Override
    public List<MessageChannel> findByStatus(Integer status) {
        return messageChannelRepository.findByStatus(status);
    }

    /**
     * 根据通道类型查询消息通道列表
     *
     * @param channelType 通道类型，如 SMS、EMAIL、IM
     * @return 消息通道列表
     */
    @Override
    public List<MessageChannel> findByChannelType(String channelType) {
        return messageChannelRepository.findByChannelType(channelType);
    }

    /**
     * 启用消息通道
     * <p>
     * 查询通道并调用enable()方法启用。
     * </p>
     *
     * @param id 通道ID
     */
    @Override
    @Transactional
    public void enable(Long id) {
        MessageChannel channel = findById(id);
        channel.enable();
        messageChannelRepository.save(channel);
        log.info("启用消息通道: id={}", id);
    }

    /**
     * 禁用消息通道
     * <p>
     * 查询通道并调用disable()方法禁用。
     * </p>
     *
     * @param id 通道ID
     */
    @Override
    @Transactional
    public void disable(Long id) {
        MessageChannel channel = findById(id);
        channel.disable();
        messageChannelRepository.save(channel);
        log.info("禁用消息通道: id={}", id);
    }
}