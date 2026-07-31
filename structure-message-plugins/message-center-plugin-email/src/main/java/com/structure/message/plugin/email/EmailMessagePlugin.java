package com.structure.message.plugin.email;

import com.structure.message.common.exception.MessageException;
import com.structure.message.common.model.MessageContext;
import com.structure.message.common.model.MessageResult;
import com.structure.message.common.plugin.MessageChannelConfig;
import com.structure.message.plugin.api.AbstractMessageChannelPlugin;
import com.structure.message.plugin.email.model.EmailRequest;
import com.structure.message.plugin.email.model.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 邮件消息插件
 */
@Slf4j
@Component
public class EmailMessagePlugin extends AbstractMessageChannelPlugin {

    private static final String CHANNEL_CODE = "EMAIL";
    private static final String CHANNEL_NAME = "邮件消息";

    @Autowired
    private EmailService emailService;

    @Override
    public String getChannelCode() {
        return CHANNEL_CODE;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    protected void doInitialize(MessageChannelConfig config) throws Exception {
        log.info("初始化邮件消息插件");

        // 验证必要的配置（支持两种格式）
        String host = config.getConfig("host");
        if (host == null) {
            host = config.getConfig("smtpHost");
        }
        
        String port = config.getConfig("port");
        if (port == null) {
            port = config.getConfig("smtpPort");
        }
        
        String username = config.getConfig("username");
        String password = config.getConfig("password");

        if (host == null || port == null || username == null || password == null) {
            throw new MessageException("EMAIL_CONFIG_ERROR", "邮件服务配置不完整");
        }

        // 初始化邮件服务
        emailService.initialize(config);

        log.info("邮件消息插件初始化成功，SMTP服务器：{}:{}", host, port);
    }

    @Override
    protected MessageResult doSend(MessageContext context) throws Exception {
        log.info("发送邮件消息，接收者：{}，内容长度：{}", context.getReceiver(),
                context.getContent() != null ? context.getContent().length() : 0);

        try {
            // 构建邮件请求
            EmailRequest request = buildEmailRequest(context);

            // 发送邮件
            EmailResponse response = emailService.sendEmail(request);

            if (response.isSuccess()) {
                log.info("邮件发送成功，接收者：{}，消息ID：{}", context.getReceiver(), response.getMessageId());
                return createSuccessResult(context, response.getMessageId());
            } else {
                log.error("邮件发送失败，接收者：{}，错误码：{}，错误信息：{}",
                        context.getReceiver(), response.getErrorCode(), response.getErrorMessage());
                return createFailureResult(context, response.getErrorCode(), response.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("邮件发送失败，接收者：{}", context.getReceiver(), e);
            throw new MessageException("EMAIL_SEND_ERROR", "邮件发送失败", e);
        }
    }

    @Override
    protected boolean doValidate(MessageContext context) {
        // 验证接收者格式（邮箱地址）
        if (context.getReceiver() == null || context.getReceiver().trim().isEmpty()) {
            log.warn("接收者不能为空");
            return false;
        }

        // 验证邮箱格式
        String email = context.getReceiver().trim();
        if (!isValidEmail(email)) {
            log.warn("邮箱格式不正确：{}", email);
            return false;
        }

        // 验证内容
        if (context.getContent() == null || context.getContent().trim().isEmpty()) {
            log.warn("消息内容不能为空");
            return false;
        }

        // 验证内容长度
        int contentLength = context.getContent().length();
        if (contentLength > 50000) { // 最大50KB
            log.warn("消息内容过长，最大长度50000，当前长度：{}", contentLength);
            return false;
        }

        // 验证模板参数（如果使用模板）
        if (context.getTemplateCode() != null) {
            if (context.getParams() == null || context.getParams().isEmpty()) {
                log.warn("模板消息需要参数");
                return false;
            }
        }

        return true;
    }

    @Override
    protected void doDestroy() throws Exception {
        log.info("销毁邮件消息插件");
        // 清理资源
        if (emailService != null) {
            emailService.destroy();
        }
    }

    /**
     * 构建邮件请求
     */
    private EmailRequest buildEmailRequest(MessageContext context) {
        EmailRequest request = new EmailRequest();
        request.setTo(context.getReceiver());
        request.setContent(context.getContent());
        request.setTemplateCode(context.getTemplateCode());
        request.setTemplateParams(context.getParams());
        request.setBusinessId(context.getBusinessId());

        // 从参数中获取邮件主题
        String subject = context.getSubject(); // 默认主题
        if (context.getParams() != null && context.getParams().containsKey("subject")) {
            subject = (String) context.getParams().get("subject");
        }
        request.setSubject(subject);

        // 从参数中获取发件人
        String from = config.getConfig("from");
        request.setFrom(from);

        // 从参数中获取抄送和密送
        if (context.getParams() != null) {
            if (context.getParams().containsKey("cc")) {
                request.setCc((String) context.getParams().get("cc"));
            }
            if (context.getParams().containsKey("bcc")) {
                request.setBcc((String) context.getParams().get("bcc"));
            }
            if (context.getParams().containsKey("attachment")) {
                request.setAttachment((String) context.getParams().get("attachment"));
            }
        }

        return request;
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // 简单的邮箱格式验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}

