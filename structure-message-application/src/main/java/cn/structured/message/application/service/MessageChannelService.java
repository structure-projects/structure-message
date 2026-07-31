package cn.structured.message.application.service;

import cn.structured.message.domain.entity.MessageChannel;

import java.util.List;

/**
 * 消息通道服务接口
 * <p>
 * 定义消息通道的业务操作，包括创建、更新、删除、查询等功能。
 * </p>
 */
public interface MessageChannelService {

    /**
     * 创建消息通道
     *
     * @param channel 消息通道实体
     * @return 创建后的消息通道实体
     */
    MessageChannel create(MessageChannel channel);

    /**
     * 更新消息通道
     *
     * @param id      通道ID
     * @param channel 消息通道实体
     * @return 更新后的消息通道实体
     */
    MessageChannel update(Long id, MessageChannel channel);

    /**
     * 删除消息通道
     *
     * @param id 通道ID
     */
    void delete(Long id);

    /**
     * 根据ID查询消息通道
     *
     * @param id 通道ID
     * @return 消息通道实体
     */
    MessageChannel findById(Long id);

    /**
     * 根据通道编码查询消息通道
     *
     * @param channelCode 通道编码
     * @return 消息通道实体
     */
    MessageChannel findByChannelCode(String channelCode);

    /**
     * 查询所有消息通道
     *
     * @return 消息通道列表
     */
    List<MessageChannel> findAll();

    /**
     * 根据状态查询消息通道列表
     *
     * @param status 状态：0-禁用，1-启用
     * @return 消息通道列表
     */
    List<MessageChannel> findByStatus(Integer status);

    /**
     * 根据通道类型查询消息通道列表
     *
     * @param channelType 通道类型，如 SMS、EMAIL、IM
     * @return 消息通道列表
     */
    List<MessageChannel> findByChannelType(String channelType);

    /**
     * 启用消息通道
     *
     * @param id 通道ID
     */
    void enable(Long id);

    /**
     * 禁用消息通道
     *
     * @param id 通道ID
     */
    void disable(Long id);
}