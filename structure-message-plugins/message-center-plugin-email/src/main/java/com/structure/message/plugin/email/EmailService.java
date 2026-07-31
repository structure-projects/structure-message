package com.structure.message.plugin.email;

import cn.structured.message.common.exception.MessageException;
import cn.structured.message.common.plugin.MessageChannelConfig;
import com.structure.message.plugin.email.model.EmailRequest;
import com.structure.message.plugin.email.model.EmailResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;
    private MessageChannelConfig config;
    private final Configuration freemarkerConfig;

    /**
     * 初始化邮件服务
     */
    public void initialize(MessageChannelConfig config) throws Exception {
        this.config = config;

        // 创建邮件发送器
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        
        // 支持两种配置格式：新格式(host, port)和旧格式(smtpHost, smtpPort)
        String host = config.getConfig("host");
        if (host == null) {
            host = config.getConfig("smtpHost");
        }
        String portStr = config.getConfig("port");
        if (portStr == null) {
            portStr = config.getConfig("smtpPort", "587");
        }
        
        sender.setHost(host);
        sender.setPort(Integer.parseInt(portStr));
        sender.setUsername(config.getConfig("username"));
        sender.setPassword(config.getConfig("password"));

        // 设置邮件属性
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", config.getConfig("auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getConfig("starttls", "true"));
        props.put("mail.smtp.connectiontimeout", config.getConfig("connectiontimeout", "5000"));
        props.put("mail.smtp.timeout", config.getConfig("timeout", "10000"));
        props.put("mail.smtp.writetimeout", config.getConfig("writetimeout", "10000"));

        // SSL配置（如果需要）
        if ("true".equals(config.getConfig("ssl", "false")) || "true".equals(config.getConfig("sslEnabled", "false"))) {
            props.put("mail.smtp.socketFactory.port", portStr);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", config.getConfig("sslProtocols", "TLSv1.2"));
        }

        this.mailSender = sender;
        log.info("邮件服务初始化成功，SMTP服务器：{}:{}", host, portStr);
    }

    /**
     * 发送邮件
     */
    public EmailResponse sendEmail(EmailRequest request) {
        try {
            log.info("开始发送邮件，收件人：{}，主题：{}", request.getTo(), request.getSubject());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 设置发件人
            String from = request.getFrom() != null ? request.getFrom() : config.getConfig("from");
            helper.setFrom(from);

            // 设置收件人
            helper.setTo(request.getTo());

            // 设置抄送
            if (request.getCc() != null) {
                helper.setCc(request.getCc());
            }

            // 设置密送
            if (request.getBcc() != null) {
                helper.setBcc(request.getBcc());
            }

            // 设置主题
            helper.setSubject(request.getSubject());

            // 设置内容
            String content = processContent(request);
            helper.setText(content, true); // true表示HTML格式

            // 添加附件
            if (request.getAttachment() != null) {
                // TODO: 实现附件处理
                log.warn("附件功能暂未实现");
            }

            // 发送邮件
            mailSender.send(message);

            String messageId = "EMAIL_" + System.currentTimeMillis();
            log.info("邮件发送成功，消息ID：{}，收件人：{}", messageId, request.getTo());
            return EmailResponse.builder()
                    .success( true)
                    .messageId(messageId)
                    .build();

        } catch (Exception e) {
            log.error("邮件发送失败，收件人：{}", request.getTo(), e);
            return EmailResponse.builder()
                    .success(false)
                    .errorCode("EMAIL_SEND_ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 处理邮件内容
     */
    private String processContent(EmailRequest request) throws Exception {
        String content = request.getContent();

        // 如果使用模板
        if (request.getTemplateCode() != null) {
            content = processTemplate(request);
        }

        // 如果内容是Markdown格式，转换为HTML（暂时禁用，因为flexmark版本不兼容）
        // if (isMarkdownContent(content)) {
        //     content = convertMarkdownToHtml(content);
        // }

        // 如果内容不是HTML格式，包装成HTML
        if (!isHtmlContent(content)) {
            content = wrapToHtml(content);
        }

        return content;
    }

    /**
     * 处理模板
     */
    private String processTemplate(EmailRequest request) throws Exception {
        try {
            String templateCode = request.getTemplateCode();
            Map<String, Object> templateParams = (Map<String, Object>) request.getTemplateParams();

            if (templateParams == null) {
                templateParams = new HashMap<>();
            }

            // 加载模板
            Template template = freemarkerConfig.getTemplate("email/" + templateCode + ".ftl");

            // 处理模板
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, templateParams);

        } catch (Exception e) {
            log.error("模板处理失败，模板编码：{}", request.getTemplateCode(), e);
            throw new MessageException("TEMPLATE_PROCESS_ERROR", "模板处理失败", e);
        }
    }

    /**
     * 判断是否是HTML内容
     */
    private boolean isHtmlContent(String content) {
        return content != null && (
                content.trim().toLowerCase().startsWith("<!doctype html>") ||
                content.trim().toLowerCase().startsWith("<html") ||
                (content.contains("<") && content.contains(">"))
        );
    }

    /**
     * 包装成HTML格式
     */
    private String wrapToHtml(String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"container\">\n");
        html.append(content.replace("\n", "<br>\n"));
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        log.info("销毁邮件服务");
        // 清理资源
    }
}
