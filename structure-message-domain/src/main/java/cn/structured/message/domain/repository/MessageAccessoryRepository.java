package cn.structured.message.domain.repository;

import cn.structure.common.repository.ICrudRepository;
import cn.structured.message.domain.entity.MessageAccessory;

import java.util.List;

/**
 * 消息附件仓储接口
 * <p>
 * 定义消息附件领域实体的数据访问操作，由基础设施层实现。
 * </p>
 */
public interface MessageAccessoryRepository extends ICrudRepository<MessageAccessory, Long> {

    /**
     * 根据消息ID查询附件列表
     *
     * @param messageId 消息ID
     * @return 消息附件列表
     */
    List<MessageAccessory> findByMessageId(Long messageId);

    /**
     * 根据消息ID删除附件
     *
     * @param messageId 消息ID
     */
    void deleteByMessageId(Long messageId);
}