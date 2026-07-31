package cn.structured.message.infra.repository;

import cn.structure.infra.repository.RepositoryFacade;
import cn.structured.message.domain.entity.MessageChannel;
import cn.structured.message.domain.repository.MessageChannelRepository;
import cn.structured.message.infra.repository.delegate.MessageChannelRepositoryDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("messageChannelRepository")
public class MessageChannelRepositoryImpl extends RepositoryFacade<MessageChannel, Long, MessageChannelRepositoryDelegate> implements MessageChannelRepository {

    @Override
    public Optional<MessageChannel> findByChannelCode(String channelCode) {
        return getDelegate().findByChannelCode(channelCode);
    }

    @Override
    public List<MessageChannel> findByStatus(Integer status) {
        return getDelegate().findByStatus(status);
    }

    @Override
    public List<MessageChannel> findByChannelType(String channelType) {
        return getDelegate().findByChannelType(channelType);
    }

    @Override
    public boolean existsByChannelCode(String channelCode) {
        return getDelegate().existsByChannelCode(channelCode);
    }

    @Override
    public boolean existsByChannelCodeAndNeId(String channelCode, Long excludeId) {
        return getDelegate().existsByChannelCodeAndNeId(channelCode, excludeId);
    }
}