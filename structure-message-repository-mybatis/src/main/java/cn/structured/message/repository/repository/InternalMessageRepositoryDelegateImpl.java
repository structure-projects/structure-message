package cn.structured.message.repository.repository;

import cn.structure.infra.annotations.WriteDelegate;
import cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate;
import cn.structured.message.domain.entity.InternalMessage;
import cn.structured.message.infra.repository.delegate.InternalMessageRepositoryDelegate;
import cn.structured.message.repository.mapper.InternalMessageMapper;
import cn.structured.message.repository.po.InternalMessagePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@WriteDelegate
public class InternalMessageRepositoryDelegateImpl extends MybatisPlusRepositoryDelegate<InternalMessage, InternalMessagePO, Long> implements InternalMessageRepositoryDelegate {

    @Override
    protected InternalMessage toEntity(InternalMessagePO po) {
        if (po == null) {
            return null;
        }
        InternalMessage entity = new InternalMessage();
        entity.setId(po.getId());
        entity.setType(po.getType());
        entity.setSender(po.getSender());
        entity.setReceiver(po.getReceiver());
        entity.setSubject(po.getSubject());
        entity.setContent(po.getContent());
        entity.setChannel(po.getChannel());
        entity.setState(po.getState());
        entity.setOrgId(po.getOrgId());
        entity.setCreateTime(po.getCreateTime());
        entity.setUpdateTime(po.getUpdateTime());
        entity.setCreateBy(po.getCreateBy());
        entity.setUpdateBy(po.getUpdateBy());
        entity.setDeleted(po.getDeleted());
        return entity;
    }

    @Override
    protected InternalMessagePO toPo(InternalMessage entity) {
        if (entity == null) {
            return null;
        }
        InternalMessagePO po = new InternalMessagePO();
        po.setId(entity.getId());
        po.setType(entity.getType());
        po.setSender(entity.getSender());
        po.setReceiver(entity.getReceiver());
        po.setSubject(entity.getSubject());
        po.setContent(entity.getContent());
        po.setChannel(entity.getChannel());
        po.setState(entity.getState());
        po.setOrgId(entity.getOrgId());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        po.setCreateBy(entity.getCreateBy());
        po.setUpdateBy(entity.getUpdateBy());
        po.setDeleted(entity.getDeleted());
        return po;
    }

    @Override
    public List<InternalMessage> findByReceiver(String receiver) {
        List<InternalMessagePO> pos = ((InternalMessageMapper) baseMapper).selectByReceiver(receiver);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<InternalMessage> findByReceiverAndState(String receiver, Integer state) {
        List<InternalMessagePO> pos = ((InternalMessageMapper) baseMapper).selectByReceiverAndState(receiver, state);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public List<InternalMessage> findByOrgId(Long orgId) {
        List<InternalMessagePO> pos = ((InternalMessageMapper) baseMapper).selectByOrgId(orgId);
        return pos.stream().map(this::toEntity).toList();
    }

    @Override
    public long countByReceiverAndState(String receiver, Integer state) {
        return ((InternalMessageMapper) baseMapper).countByReceiverAndState(receiver, state);
    }
}