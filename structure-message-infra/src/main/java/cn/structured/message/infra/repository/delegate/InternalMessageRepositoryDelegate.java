package cn.structured.message.infra.repository.delegate;

import cn.structure.infra.repository.RepositoryDelegate;
import cn.structured.message.domain.entity.InternalMessage;

import java.util.List;

public interface InternalMessageRepositoryDelegate extends RepositoryDelegate<InternalMessage, Long> {

    List<InternalMessage> findByReceiver(String receiver);

    List<InternalMessage> findByReceiverAndState(String receiver, Integer state);

    List<InternalMessage> findByOrgId(Long orgId);

    long countByReceiverAndState(String receiver, Integer state);
}