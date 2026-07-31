package cn.structured.message.infra.repository.delegate;

import cn.structure.infra.repository.RepositoryDelegate;
import cn.structured.message.domain.entity.MessageRecord;

import java.util.List;

public interface MessageRecordRepositoryDelegate extends RepositoryDelegate<MessageRecord, Long> {

    List<MessageRecord> findByBusinessId(String businessId);

    List<MessageRecord> findByChannelId(Long channelId);

    List<MessageRecord> findByStatus(Integer status);

    List<MessageRecord> findByBusinessIdAndChannelId(String businessId, Long channelId);

    List<MessageRecord> findByReceiver(String receiver);

    List<MessageRecord> findPendingMessages();

    long countByStatus(Integer status);
}