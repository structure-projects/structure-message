package com.structure.message.plugin.email.model;

import lombok.Data;

/**
 * 邮件请求
 */
@Data
public class EmailRequest {

    /**
     * 发件人
     */
    private String from;

    /**
     * 收件人
     */
    private String to;

    /**
     * 抄送
     */
    private String cc;

    /**
     * 密送
     */
    private String bcc;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板参数
     */
    private Object templateParams;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 业务ID
     */
    private String businessId;
}
