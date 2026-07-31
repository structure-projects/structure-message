package cn.structured.message.infra.repository.delegate;

import cn.structure.infra.repository.RepositoryDelegate;
import cn.structured.message.domain.entity.MessageAccessory;

import java.util.List;

public interface MessageAccessoryRepositoryDelegate extends RepositoryDelegate<MessageAccessory, Long> {

    List<MessageAccessory> findByMessageId(Long messageId);

    void deleteByMessageId(Long messageId);
}