package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.common.dto.MessageRecordQuery;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.infra.repository.delegate.MessageRecordRepositoryDelegate;
import cn.structured.message.repository.mapper.MessageRecordMapper;
import cn.structured.message.repository.po.MessageRecordPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public List<MessageRecord> findByQuery(MessageRecordQuery query, long offset, long size) {
        LambdaQueryWrapper<MessageRecordPO> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(MessageRecordPO::getCreateTime);

        Page<MessageRecordPO> page = new Page<>(offset / size + 1, size);
        Page<MessageRecordPO> result = baseMapper.selectPage(page, wrapper);
        return result.getRecords().stream().map(this::toEntity).toList();
    }

    @Override
    public long countByQuery(MessageRecordQuery query) {
        LambdaQueryWrapper<MessageRecordPO> wrapper = buildQueryWrapper(query);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<MessageRecordPO> buildQueryWrapper(MessageRecordQuery query) {
        LambdaQueryWrapper<MessageRecordPO> wrapper = new LambdaQueryWrapper<>();
        if (query.getChannelId() != null) {
            wrapper.eq(MessageRecordPO::getChannelId, query.getChannelId());
        }
        if (query.getReceiver() != null && !query.getReceiver().isBlank()) {
            wrapper.eq(MessageRecordPO::getReceiver, query.getReceiver());
        }
        if (query.getStatus() != null) {
            wrapper.eq(MessageRecordPO::getStatus, query.getStatus());
        }
        if (query.getBusinessSource() != null && !query.getBusinessSource().isBlank()) {
            wrapper.eq(MessageRecordPO::getBusinessSource, query.getBusinessSource());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(MessageRecordPO::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(MessageRecordPO::getCreateTime, query.getEndTime());
        }
        return wrapper;
    }
}