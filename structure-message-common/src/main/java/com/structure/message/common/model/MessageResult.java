package com.structure.message.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息发送结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 接收者
     */
    private String receiver;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 响应数据
     */
    private Object responseData;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    /**
     * 创建成功结果
     */
    public static MessageResult success(Long messageId, String channelCode, String receiver, Object responseData) {
        return MessageResult.builder()
                .success(true)
                .messageId(messageId)
                .channelCode(channelCode)
                .receiver(receiver)
                .responseData(responseData)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static MessageResult failure(String channelCode, String receiver, String errorCode, String errorMsg) {
        return MessageResult.builder()
                .success(false)
                .channelCode(channelCode)
                .receiver(receiver)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }
}