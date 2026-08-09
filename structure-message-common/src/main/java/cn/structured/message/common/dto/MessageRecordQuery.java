package cn.structured.message.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息记录查询参数
 *
 * @author structure-message
 * @version 1.0.0
 */
@Data
public class MessageRecordQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通道ID
     */
    private Long channelId;

    /**
     * 接收人
     */
    private String receiver;

    /**
     * 状态：0-待发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer status;

    /**
     * 业务来源
     */
    private String businessSource;

    /**
     * 查询开始时间
     */
    private LocalDateTime startTime;

    /**
     * 查询结束时间
     */
    private LocalDateTime endTime;
}
