package cn.structured.message.application.service;

import cn.structure.common.vo.ReqPage;
import cn.structure.common.vo.ResPage;
import cn.structured.message.common.dto.MessageRecordQuery;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.vo.MessageRecordVO;

/**
 * 消息记录查询服务接口
 * <p>
 * 提供消息发送记录的分页查询、详情查看和重发功能。
 * </p>
 *
 * @author structure-message
 * @version 1.0.0
 */
public interface MessageRecordService {

    /**
     * 分页查询消息记录
     *
     * @param query   查询条件
     * @param reqPage 分页参数
     * @return 分页结果
     */
    ResPage<MessageRecordVO> page(MessageRecordQuery query, ReqPage reqPage);

    /**
     * 根据ID查询消息记录详情
     *
     * @param id 消息记录ID
     * @return 消息记录视图对象
     */
    MessageRecordVO findById(Long id);

    /**
     * 重发消息
     *
     * @param id 消息记录ID
     * @return 重发结果
     */
    MessageResult resend(Long id);
}
