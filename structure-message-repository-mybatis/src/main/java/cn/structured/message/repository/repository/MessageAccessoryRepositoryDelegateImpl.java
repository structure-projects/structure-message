package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.domain.entity.MessageAccessory;
import cn.structured.message.infra.repository.delegate.MessageAccessoryRepositoryDelegate;
import cn.structured.message.repository.mapper.MessageAccessoryMapper;
import cn.structured.message.repository.po.MessageAccessoryPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@WriteDelegate
public class MessageAccessoryRepositoryDelegateImpl extends MybatisPlusRepositoryDelegate<MessageAccessory, MessageAccessoryPO, Long> implements MessageAccessoryRepositoryDelegate {

    @Override
    protected MessageAccessory toEntity(MessageAccessoryPO po) {
        if (po == null) {
            return null;
        }
        MessageAccessory entity = BeanUtils.instantiateClass(MessageAccessory.class);
        entity.setId(po.getId());
        setField(entity, "messageId", po.getMessageId());
        setField(entity, "resourceType", po.getResourceType());
        setField(entity, "resourceId", po.getResourceId());
        setField(entity, "resourceName", po.getResourceName());
        setField(entity, "resourceIcon", po.getResourceIcon());
        setField(entity, "resourceCode", po.getResourceCode());
        setField(entity, "resourceDesc", po.getResourceDesc());
        setField(entity, "amount", po.getAmount());
        setField(entity, "state", po.getState());
        setField(entity, "createTime", po.getCreateTime());
        setField(entity, "updateTime", po.getUpdateTime());
        return entity;
    }

    @Override
    protected MessageAccessoryPO toPo(MessageAccessory entity) {
        if (entity == null) {
            return null;
        }
        MessageAccessoryPO po = new MessageAccessoryPO();
        po.setId(entity.getId());
        po.setMessageId(entity.getMessageId());
        po.setResourceType(entity.getResourceType());
        po.setResourceId(entity.getResourceId());
        po.setResourceName(entity.getResourceName());
        po.setResourceIcon(entity.getResourceIcon());
        po.setResourceCode(entity.getResourceCode());
        po.setResourceDesc(entity.getResourceDesc());
        po.setAmount(entity.getAmount());
        po.setState(entity.getState());
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
    public List<MessageAccessory> findByMessageId(Long messageId) {
        List<MessageAccessoryPO> pos = ((MessageAccessoryMapper) baseMapper).selectByMessageId(messageId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public void deleteByMessageId(Long messageId) {
        ((MessageAccessoryMapper) baseMapper).deleteByMessageId(messageId);
    }
}