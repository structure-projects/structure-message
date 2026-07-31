package cn.structured.message.infra.repository;

import cn.structure.infra.repository.RepositoryFacade;
import cn.structured.message.domain.entity.MessageAccessory;
import cn.structured.message.domain.repository.MessageAccessoryRepository;
import cn.structured.message.infra.repository.delegate.MessageAccessoryRepositoryDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("messageAccessoryRepository")
public class MessageAccessoryRepositoryImpl extends RepositoryFacade<MessageAccessory, Long, MessageAccessoryRepositoryDelegate> implements MessageAccessoryRepository {

    @Override
    public List<MessageAccessory> findByMessageId(Long messageId) {
        return getDelegate().findByMessageId(messageId);
    }

    @Override
    public void deleteByMessageId(Long messageId) {
        getDelegate().deleteByMessageId(messageId);
    }
}