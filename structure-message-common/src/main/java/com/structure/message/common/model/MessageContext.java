package com.structure.message.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织ID（可选）
     */
    private Long orgId;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 通道编码
     */
    @NotBlank(message = "通道编码不能为空")
    private String channelCode;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 接收者
     */
    @NotBlank(message = "接收者不能为空")
    private String receiver;

    /**
     * 消息内容（无模板时使用）
     */
    private String content;

    /**
     * 模板参数
     */
    private Map<String, Object> params;

    /**
     * 扩展参数
     */
    private Map<String, Object> extParams;

    /**
     * 优先级（1-10，数字越大优先级越高）
     */
    private Integer priority = 5;

    /**
     * 重试次数
     */
    private Integer retryTimes = 0;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes = 3;

    /**
     * 超时时间（毫秒）
     */
    private Long timeout = 30000L;

    /**
     *  服务提供商（可选，用于在发送时动态选择）
     */
    private String provider;
}