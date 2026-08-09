package cn.structured.message.application.assembler;

import cn.structured.message.common.vo.MessageRecordVO;
import cn.structured.message.domain.entity.MessageRecord;

import java.util.List;

/**
 * 消息记录组装器
 * <p>
 * 负责领域实体与视图对象之间的转换。
 * </p>
 *
 * @author structure-message
 * @version 1.0.0
 */
public final class MessageRecordAssembler {

    private MessageRecordAssembler() {
    }

    /**
     * 将领域实体转换为视图对象
     *
     * @param entity 消息记录实体
     * @return 消息记录视图对象
     */
    public static MessageRecordVO toVO(MessageRecord entity) {
        if (entity == null) {
            return null;
        }
        MessageRecordVO vo = new MessageRecordVO();
        vo.setId(entity.getId());
        vo.setOrgId(entity.getOrgId());
        vo.setBusinessId(entity.getBusinessId());
        vo.setTemplateId(entity.getTemplateId());
        vo.setChannelId(entity.getChannelId());
        vo.setChannelCode(entity.getChannelCode());
        vo.setReceiver(entity.getReceiver());
        vo.setContent(entity.getContent());
        vo.setParams(entity.getParams());
        vo.setSubject(entity.getSubject());
        vo.setBusinessSource(entity.getBusinessSource());
        vo.setStatus(entity.getStatus());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setSendTime(entity.getSendTime());
        vo.setRetryTimes(entity.getRetryTimes());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 将领域实体列表转换为视图对象列表
     *
     * @param entities 消息记录实体列表
     * @return 消息记录视图对象列表
     */
    public static List<MessageRecordVO> toVOList(List<MessageRecord> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream().map(MessageRecordAssembler::toVO).toList();
    }
}
