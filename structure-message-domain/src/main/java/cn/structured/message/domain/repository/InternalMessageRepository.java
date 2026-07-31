package cn.structured.message.domain.repository;

import cn.structure.common.repository.ICrudRepository;
import cn.structured.message.domain.entity.InternalMessage;

import java.util.List;

/**
 * 内部消息仓储接口
 * <p>
 * 定义内部消息领域实体的数据访问操作，由基础设施层实现。
 * </p>
 */
public interface InternalMessageRepository extends ICrudRepository<InternalMessage, Long> {

    /**
     * 根据接收人查询消息列表
     *
     * @param receiver 接收人ID
     * @return 内部消息列表
     */
    List<InternalMessage> findByReceiver(String receiver);

    /**
     * 根据接收人和状态查询消息列表
     *
     * @param receiver 接收人ID
     * @param state    状态：0-未读，1-已读
     * @return 内部消息列表
     */
    List<InternalMessage> findByReceiverAndState(String receiver, Integer state);

    /**
     * 根据机构ID查询消息列表
     *
     * @param orgId 机构ID
     * @return 内部消息列表
     */
    List<InternalMessage> findByOrgId(Long orgId);

    /**
     * 根据接收人和状态统计消息数量
     *
     * @param receiver 接收人ID
     * @param state    状态
     * @return 消息数量
     */
    long countByReceiverAndState(String receiver, Integer state);
}