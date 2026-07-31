package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.infra.repository.delegate.MessageRecordRepositoryDelegate;
import cn.structured.message.repository.mapper.MessageRecordMapper;
import cn.structured.message.repository.po.MessageRecordPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@WriteDelegate
public class MessageRecordRepositoryDelegateImpl extends MybatisPlusRepositoryDelegate<MessageRecord, MessageRecordPO, Long> implements MessageRecordRepositoryDelegate {

    @Override
    protected MessageRecord toEntity(MessageRecordPO po) {
        if (po == null) {
            return null;
        }
        MessageRecord entity = BeanUtils.instantiateClass(MessageRecord.class);
        entity.setId(po.getId());
        entity.setChannelId(po.getChannelId());
        entity.setTemplateId(po.getTemplateId());
        setField(entity, "orgId", po.getOrgId());
        setField(entity, "businessId", po.getBusinessId());
        setField(entity, "channelCode", po.getChannelCode());
        setField(entity, "receiver", po.getReceiver());
        setField(entity, "content", po.getContent());
        setField(entity, "params", po.getParams());
        setField(entity, "subject", po.getSubject());
        setField(entity, "businessSource", po.getBusinessSource());
        setField(entity, "status", po.getStatus());
        setField(entity, "errorMsg", po.getErrorMsg());
        setField(entity, "sendTime", po.getSendTime());
        setField(entity, "retryTimes", po.getRetryTimes());
        setField(entity, "createTime", po.getCreateTime());
        setField(entity, "updateTime", po.getUpdateTime());
        return entity;
    }

    @Override
    protected MessageRecordPO toPo(MessageRecord entity) {
        if (entity == null) {
            return null;
        }
        MessageRecordPO po = new MessageRecordPO();
        po.setId(entity.getId());
        po.setOrgId(entity.getOrgId());
        po.setBusinessId(entity.getBusinessId());
        po.setTemplateId(entity.getTemplateId());
        po.setChannelId(entity.getChannelId());
        po.setChannelCode(entity.getChannelCode());
        po.setReceiver(entity.getReceiver());
        po.setContent(entity.getContent());
        po.setParams(entity.getParams());
        po.setSubject(entity.getSubject());
        po.setBusinessSource(entity.getBusinessSource());
        po.setStatus(entity.getStatus());
        po.setErrorMsg(entity.getErrorMsg());
        po.setSendTime(entity.getSendTime());
        po.setRetryTimes(entity.getRetryTimes());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' on " + target.getClass().getSimpleName(), e);
        }
    }

    @Override
    public List<MessageRecord> findByBusinessId(String businessId) {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectByBusinessId(businessId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageRecord> findByChannelId(Long channelId) {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectByChannelId(channelId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageRecord> findByStatus(Integer status) {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectByStatus(status);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageRecord> findByBusinessIdAndChannelId(String businessId, Long channelId) {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectByBusinessIdAndChannelId(businessId, channelId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageRecord> findByReceiver(String receiver) {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectByReceiver(receiver);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageRecord> findPendingMessages() {
        List<MessageRecordPO> pos = ((MessageRecordMapper) baseMapper).selectPendingMessages();
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public long countByStatus(Integer status) {
        return ((MessageRecordMapper) baseMapper).countByStatus(status);
    }
}