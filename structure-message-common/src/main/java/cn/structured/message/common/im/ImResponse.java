package cn.structured.message.common.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IM消息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 原始响应数据
     */
    private Object rawResponse;

}
