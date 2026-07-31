package cn.structured.message.domain.repository;

import cn.structure.common.repository.ICrudRepository;
import cn.structured.message.domain.entity.MessageRecord;

import java.util.List;

/**
 * 消息记录仓储接口
 * <p>
 * 定义消息记录领域实体的数据访问操作，由基础设施层实现。
 * </p>
 */
public interface MessageRecordRepository extends ICrudRepository<MessageRecord, Long> {

    /**
     * 根据业务ID查询消息记录列表
     *
     * @param businessId 业务ID
     * @return 消息记录列表
     */
    List<MessageRecord> findByBusinessId(String businessId);

    /**
     * 根据通道ID查询消息记录列表
     *
     * @param channelId 通道ID
     * @return 消息记录列表
     */
    List<MessageRecord> findByChannelId(Long channelId);

    /**
     * 根据状态查询消息记录列表
     *
     * @param status 状态：0-待发送，1-发送中，2-发送成功，3-发送失败
     * @return 消息记录列表
     */
    List<MessageRecord> findByStatus(Integer status);

    /**
     * 根据业务ID和通道ID查询消息记录列表
     *
     * @param businessId 业务ID
     * @param channelId  通道ID
     * @return 消息记录列表
     */
    List<MessageRecord> findByBusinessIdAndChannelId(String businessId, Long channelId);

    /**
     * 根据接收人查询消息记录列表
     *
     * @param receiver 接收人
     * @return 消息记录列表
     */
    List<MessageRecord> findByReceiver(String receiver);

    /**
     * 查询待发送的消息列表
     *
     * @return 待发送消息列表
     */
    List<MessageRecord> findPendingMessages();

    /**
     * 根据状态统计消息数量
     *
     * @param status 状态
     * @return 消息数量
     */
    long countByStatus(Integer status);
}