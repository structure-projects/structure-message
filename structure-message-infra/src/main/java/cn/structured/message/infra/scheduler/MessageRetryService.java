package cn.structured.message.infra.scheduler;

import cn.structured.message.domain.entity.MessageRecord;

/**
 * 消息重试服务接口
 * <p>
 * 定义消息重试的业务操作。
 * </p>
 */
public interface MessageRetryService {

    /**
     * 重试消息发送
     *
     * @param record 消息记录实体
     * @return 是否重试成功
     */
    boolean retryMessage(MessageRecord record);
}