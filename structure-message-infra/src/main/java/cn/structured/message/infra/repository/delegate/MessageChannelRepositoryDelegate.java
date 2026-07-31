package cn.structured.message.infra.repository.delegate;

import cn.structure.infra.repository.RepositoryDelegate;
import cn.structured.message.domain.entity.MessageChannel;

import java.util.List;
import java.util.Optional;

public interface MessageChannelRepositoryDelegate extends RepositoryDelegate<MessageChannel, Long> {

    Optional<MessageChannel> findByChannelCode(String channelCode);

    List<MessageChannel> findByStatus(Integer status);

    List<MessageChannel> findByChannelType(String channelType);

    boolean existsByChannelCode(String channelCode);

    boolean existsByChannelCodeAndNeId(String channelCode, Long excludeId);
}