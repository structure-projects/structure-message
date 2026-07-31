package cn.structured.message.domain.service;

import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.entity.MessageRecord;

/**
 * 消息领域服务接口
 * <p>
 * 封装消息相关的核心业务逻辑，包括消息发送、通道配置验证、发送失败处理等。
 * 领域服务作为领域层的一部分，处理跨实体的业务逻辑。
 * </p>
 *
 * @author chuck
 * @since 1.0.0
 */
public interface IMessageDomainService {

    /**
     * 创建消息记录
     * <p>
     * 根据业务参数创建消息发送记录，包含参数验证和状态初始化。
     * </p>
     *
     * @param orgId         机构ID
     * @param businessId    业务ID
     * @param channelCode   通道编码
     * @param receiver      接收人
     * @param content       消息内容
     * @param params        参数JSON
     * @param subject       消息主题
     * @param businessSource 业务来源
     * @return 创建的消息记录实体
     */
    MessageRecord createMessageRecord(Long orgId, String businessId, String channelCode, String receiver,
                                      String content, String params, String subject, String businessSource);

    /**
     * 验证通道配置有效性
     * <p>
     * 验证通道配置是否存在且状态正常，确保消息能正常发送。
     * </p>
     *
     * @param channelConfig 通道配置实体
     */
    void validateChannelConfig(ChannelConfig channelConfig);

    /**
     * 处理发送失败
     * <p>
     * 当消息发送失败时，更新消息记录状态并记录错误信息。
     * </p>
     *
     * @param messageRecordId 消息记录ID
     * @param errorMessage    错误信息
     */
    void handleSendFailure(Long messageRecordId, String errorMessage);

    /**
     * 标记消息发送成功
     * <p>
     * 更新消息记录状态为发送成功。
     * </p>
     *
     * @param messageRecordId 消息记录ID
     */
    void markSendSuccess(Long messageRecordId);
}
