package cn.structured.message.infra.repository;

import cn.structure.infra.repository.RepositoryFacade;
import cn.structured.message.domain.entity.InternalMessage;
import cn.structured.message.domain.repository.InternalMessageRepository;
import cn.structured.message.infra.repository.delegate.InternalMessageRepositoryDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("internalMessageRepository")
public class InternalMessageRepositoryImpl extends RepositoryFacade<InternalMessage, Long, InternalMessageRepositoryDelegate> implements InternalMessageRepository {

    @Override
    public List<InternalMessage> findByReceiver(String receiver) {
        return getDelegate().findByReceiver(receiver);
    }

    @Override
    public List<InternalMessage> findByReceiverAndState(String receiver, Integer state) {
        return getDelegate().findByReceiverAndState(receiver, state);
    }

    @Override
    public List<InternalMessage> findByOrgId(Long orgId) {
        return getDelegate().findByOrgId(orgId);
    }

    @Override
    public long countByReceiverAndState(String receiver, Integer state) {
        return getDelegate().countByReceiverAndState(receiver, state);
    }
}