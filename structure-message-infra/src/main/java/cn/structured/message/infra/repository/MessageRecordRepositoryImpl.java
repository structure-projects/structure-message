package cn.structured.message.infra.repository;

import cn.structure.infra.repository.RepositoryFacade;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.repository.MessageRecordRepository;
import cn.structured.message.infra.repository.delegate.MessageRecordRepositoryDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("messageRecordRepository")
public class MessageRecordRepositoryImpl extends RepositoryFacade<MessageRecord, Long, MessageRecordRepositoryDelegate> implements MessageRecordRepository {

    @Override
    public List<MessageRecord> findByBusinessId(String businessId) {
        return getDelegate().findByBusinessId(businessId);
    }

    @Override
    public List<MessageRecord> findByChannelId(Long channelId) {
        return getDelegate().findByChannelId(channelId);
    }

    @Override
    public List<MessageRecord> findByStatus(Integer status) {
        return getDelegate().findByStatus(status);
    }

    @Override
    public List<MessageRecord> findByBusinessIdAndChannelId(String businessId, Long channelId) {
        return getDelegate().findByBusinessIdAndChannelId(businessId, channelId);
    }

    @Override
    public List<MessageRecord> findByReceiver(String receiver) {
        return getDelegate().findByReceiver(receiver);
    }

    @Override
    public List<MessageRecord> findPendingMessages() {
        return getDelegate().findPendingMessages();
    }

    @Override
    public long countByStatus(Integer status) {
        return getDelegate().countByStatus(status);
    }
}