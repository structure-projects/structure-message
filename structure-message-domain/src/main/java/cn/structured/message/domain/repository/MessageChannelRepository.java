package cn.structured.message.domain.repository;

import cn.structure.common.repository.ICrudRepository;
import cn.structured.message.domain.entity.MessageChannel;

import java.util.List;
import java.util.Optional;

/**
 * 消息通道仓储接口
 * <p>
 * 定义消息通道领域实体的数据访问操作，由基础设施层实现。
 * </p>
 */
public interface MessageChannelRepository extends ICrudRepository<MessageChannel, Long> {

    /**
     * 根据通道编码查询通道
     *
     * @param channelCode 通道编码
     * @return 消息通道实体
     */
    Optional<MessageChannel> findByChannelCode(String channelCode);

    /**
     * 根据状态查询通道列表
     *
     * @param status 状态：0-禁用，1-启用
     * @return 消息通道列表
     */
    List<MessageChannel> findByStatus(Integer status);

    /**
     * 根据通道类型查询通道列表
     *
     * @param channelType 通道类型，如 SMS、EMAIL、IM
     * @return 消息通道列表
     */
    List<MessageChannel> findByChannelType(String channelType);

    /**
     * 判断通道编码是否存在
     *
     * @param channelCode 通道编码
     * @return true-存在，false-不存在
     */
    boolean existsByChannelCode(String channelCode);

    /**
     * 判断通道编码是否存在（排除指定ID）
     *
     * @param channelCode 通道编码
     * @param excludeId   排除的ID
     * @return true-存在，false-不存在
     */
    boolean existsByChannelCodeAndNeId(String channelCode, Long excludeId);
}