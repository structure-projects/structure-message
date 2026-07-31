package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.domain.entity.MessageChannel;
import cn.structured.message.infra.repository.delegate.MessageChannelRepositoryDelegate;
import cn.structured.message.repository.mapper.MessageChannelMapper;
import cn.structured.message.repository.po.MessageChannelPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@WriteDelegate
public class MessageChannelRepositoryDelegateImpl extends MybatisPlusRepositoryDelegate<MessageChannel, MessageChannelPO, Long> implements MessageChannelRepositoryDelegate {

    @Override
    protected MessageChannel toEntity(MessageChannelPO po) {
        if (po == null) {
            return null;
        }
        MessageChannel entity = BeanUtils.instantiateClass(MessageChannel.class);
        entity.setId(po.getId());
        setField(entity, "channelCode", po.getChannelCode());
        setField(entity, "channelName", po.getChannelName());
        setField(entity, "channelType", po.getChannelType());
        setField(entity, "pluginClass", po.getPluginClass());
        setField(entity, "status", po.getStatus() != null ? MessageChannel.Status.of(po.getStatus()) : null);
        setField(entity, "createTime", po.getCreateTime());
        setField(entity, "updateTime", po.getUpdateTime());
        return entity;
    }

    @Override
    protected MessageChannelPO toPo(MessageChannel entity) {
        if (entity == null) {
            return null;
        }
        MessageChannelPO po = new MessageChannelPO();
        po.setId(entity.getId());
        po.setChannelCode(entity.getChannelCode());
        po.setChannelName(entity.getChannelName());
        po.setChannelType(entity.getChannelType());
        po.setPluginClass(entity.getPluginClass());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
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
    public Optional<MessageChannel> findByChannelCode(String channelCode) {
        MessageChannelPO po = ((MessageChannelMapper) baseMapper).selectByChannelCode(channelCode);
        return Optional.ofNullable(toEntity(po));
    }

    @Override
    public List<MessageChannel> findByStatus(Integer status) {
        List<MessageChannelPO> pos = ((MessageChannelMapper) baseMapper).selectByStatus(status);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<MessageChannel> findByChannelType(String channelType) {
        List<MessageChannelPO> pos = ((MessageChannelMapper) baseMapper).selectByChannelType(channelType);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public boolean existsByChannelCode(String channelCode) {
        return ((MessageChannelMapper) baseMapper).countByChannelCode(channelCode) > 0;
    }

    @Override
    public boolean existsByChannelCodeAndNeId(String channelCode, Long excludeId) {
        return ((MessageChannelMapper) baseMapper).countByChannelCodeAndNeId(channelCode, excludeId) > 0;
    }
}