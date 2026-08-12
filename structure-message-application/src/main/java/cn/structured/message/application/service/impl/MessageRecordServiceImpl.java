package cn.structured.message.application.service.impl;

import cn.structure.common.exception.CommonException;
import cn.structure.common.vo.ReqPage;
import cn.structure.common.vo.ResPage;
import cn.structured.message.application.assembler.MessageRecordAssembler;
import cn.structured.message.application.service.MessageRecordService;
import cn.structured.message.application.service.MessageService;
import cn.structured.message.common.dto.MessageRecordQuery;
import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.vo.MessageRecordVO;
import cn.structured.message.domain.entity.MessageRecord;
import cn.structured.message.domain.repository.MessageRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息记录查询服务实现
 *
 * @author structure-message
 * @version 1.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageRecordServiceImpl implements MessageRecordService {

    private final MessageRecordRepository messageRecordRepository;
    private final MessageService messageService;

    @Override
    public ResPage<MessageRecordVO> page(MessageRecordQuery query, ReqPage reqPage) {
        long size = reqPage.getSize();
        long offset = (long) (reqPage.getPage() - 1) * size;

        List<MessageRecord> records = messageRecordRepository.findByQuery(query, offset, size);
        long total = messageRecordRepository.countByQuery(query);

        ResPage<MessageRecordVO> resPage = new ResPage<>();
        resPage.setCurrent(reqPage.getPage().longValue());
        resPage.setSize(reqPage.getSize().longValue());
        resPage.setTotal(total);
        resPage.setPages((total + reqPage.getSize() - 1) / reqPage.getSize());
        resPage.setRecords(MessageRecordAssembler.toVOList(records));
        return resPage;
    }

    @Override
    public MessageRecordVO findById(Long id) {
        MessageRecord record = messageRecordRepository.findById(id);
        if (record == null) {
            throw new CommonException("MESSAGE_RECORD_NOT_FOUND", "消息记录不存在: " + id);
        }
        return MessageRecordAssembler.toVO(record);
    }

    @Override
    public MessageResult resend(Long id) {
        MessageRecord record = messageRecordRepository.findById(id);
        if (record == null) {
            throw new CommonException("MESSAGE_RECORD_NOT_FOUND", "消息记录不存在: " + id);
        }

        log.info("重发消息: id={}, channelCode={}, receiver={}", id, record.getChannelCode(), record.getReceiver());

        MessageContext context = MessageContext.builder()
                .messageId(record.getId())
                .orgId(record.getOrgId())
                .businessId(record.getBusinessId())
                .channelCode(record.getChannelCode())
                .receiver(record.getReceiver())
                .content(record.getContent())
                .subject(record.getSubject())
                .businessSource(record.getBusinessSource())
                .retryTimes(record.getRetryTimes() != null ? record.getRetryTimes() + 1 : 1)
                .build();

        return messageService.sendSync(context);
    }
}
