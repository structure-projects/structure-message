package com.structure.message.plugin.im.dingtalk;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.structure.message.common.exception.MessageException;
import com.structure.message.common.im.ImProvider;
import com.structure.message.common.im.ImRequest;
import com.structure.message.common.im.ImResponse;
import com.structure.message.common.plugin.MessageChannelConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 钉钉IM服务提供商
 */
@Slf4j
@Component
public class DingtalkImProvider implements ImProvider {

    private static final String PROVIDER_NAME = "dingtalk";
    private static final String CHANNEL_CODE = "DINGTALK";
    private static final String DINGTALK_API_BASE = "https://oapi.dingtalk.com";
    private static final String ACCESS_TOKEN_URL = DINGTALK_API_BASE + "/gettoken";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpClient;
    private String appId;
    private String appSecret;
    private String webhookUrl;
    private String signSecret;
    private String botId;
    private String accessToken;
    private Long tokenExpireTime;
    private String keyword;

    @Override
    public ImResponse sendImMessage(ImRequest request) throws Exception {
        String messageType = request.getMessageType();
        String receiver = request.getReceiver();
        
        // 如果明确指定为P2P，走点对点消息路径
        if ("P2P".equals(messageType)) {
            return sendP2PMessage(request);
        }
        // 如果receiver以chat_开头，使用应用机器人发群聊
        if (receiver != null && receiver.startsWith("chat_")) {
            return sendAppBotToChat(request);
        }
        if ("BOT".equals(messageType) || webhookUrl != null) {
            return sendBotMessage(request);
        } else {
            return sendP2PMessage(request);
        }
    }

    private ImResponse sendBotMessage(ImRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();

        String content = request.getContent();
        if (keyword != null && !keyword.isEmpty() && !content.contains(keyword)) {
            content = keyword + " - " + content;
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", request.getTitle());
            markdown.put("text", content);
            body.put("msgtype", "markdown");
            body.put("markdown", markdown);
        } else {
            Map<String, Object> text = new HashMap<>();
            text.put("content", content);
            body.put("msgtype", "text");
            body.put("text", text);
        }

        if (request.getReceiver() != null && !request.getReceiver().isEmpty()) {
            Map<String, Object> at = new HashMap<>();
            at.put("atUserIds", new String[]{request.getReceiver()});
            at.put("isAtAll", false);
            body.put("at", at);
        }

        String url = webhookUrl;
        if (signSecret != null && !signSecret.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            String sign = generateSign(timestamp, signSecret);
            url = url + "&timestamp=" + timestamp + "&sign=" + sign;
        }

        String response = httpPost(url, body);
        JSONObject jsonResponse = JSON.parseObject(response);

        int code = jsonResponse.getIntValue("code");
        if (code != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("DINGTALK_SEND_ERROR")
                    .errorMessage("钉钉API错误: code=" + code + ", msg=" + jsonResponse.getString("msg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .rawResponse(jsonResponse)
                .build();
    }

    private ImResponse sendAppBotToChat(ImRequest request) throws Exception {
        String token = getAccessToken();

        if (botId == null || botId.isEmpty()) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("DINGTALK_CONFIG_ERROR")
                    .errorMessage("缺少agent_id配置，请在配置中设置botId")
                    .build();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", request.getReceiver());
        
        Map<String, Object> msg = new HashMap<>();
        msg.put("msgtype", "text");
        
        Map<String, Object> text = new HashMap<>();
        text.put("content", request.getContent());
        msg.put("text", text);
        
        body.put("msg", msg);

        String response = httpPost(
                DINGTALK_API_BASE + "/topapi/chat/send?access_token=" + token,
                body
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        int code = jsonResponse.getIntValue("errcode");
        if (code != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("DINGTALK_SEND_ERROR")
                    .errorMessage("钉钉API错误: errcode=" + code + ", errmsg=" + jsonResponse.getString("errmsg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getString("message_id"))
                .rawResponse(jsonResponse)
                .build();
    }

    private ImResponse sendP2PMessage(ImRequest request) throws Exception {
        String token = getAccessToken();

        if (botId == null || botId.isEmpty()) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("DINGTALK_CONFIG_ERROR")
                    .errorMessage("缺少agent_id配置，请在配置中设置botId")
                    .build();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("userid_list", request.getReceiver());
        body.put("agent_id", botId);

        Map<String, Object> msg = new HashMap<>();
        msg.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();
        text.put("content", request.getContent());
        msg.put("text", text);

        body.put("msg", msg);

        String response = httpPost(
                DINGTALK_API_BASE + "/topapi/message/corpconversation/asyncsend_v2?access_token=" + token,
                body
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        int code = jsonResponse.getIntValue("code");
        if (code != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("DINGTALK_SEND_ERROR")
                    .errorMessage("钉钉API错误: code=" + code + ", msg=" + jsonResponse.getString("msg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getString("task_id"))
                .rawResponse(jsonResponse)
                .build();
    }

    private String getAccessToken() throws Exception {
        if (accessToken != null && tokenExpireTime != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        String url = ACCESS_TOKEN_URL + "?appkey=" + appId + "&appsecret=" + appSecret;
        String response = httpGet(url);
        JSONObject jsonResponse = JSON.parseObject(response);

        if (jsonResponse.getIntValue("errcode") != 0) {
            throw new MessageException("DINGTALK_TOKEN_ERROR", "获取钉钉访问令牌失败：" + jsonResponse.getString("errmsg"));
        }

        accessToken = jsonResponse.getString("access_token");
        int expire = jsonResponse.getIntValue("expires_in");
        tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;

        return accessToken;
    }

    private String generateSign(long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(new byte[]{});
        return java.net.URLEncoder.encode(java.util.Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8.name());
    }

    private String httpPost(String url, Object body) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON.toJSONString(body), JSON_MEDIA_TYPE))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            throw new Exception("响应为空");
        }
    }

    private String httpGet(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            throw new Exception("响应为空");
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getChannelCode() {
        return CHANNEL_CODE;
    }

    @Override
    public void destroy() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        log.info("钉钉IM服务已销毁");
    }

    @Override
    public void initialize(MessageChannelConfig config) {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.appId = config.getConfig("appId");
        this.appSecret = config.getConfig("appSecret");
        this.webhookUrl = config.getConfig("webhookUrl");
        this.signSecret = config.getConfig("signSecret");
        this.botId = config.getConfig("botId");
        this.keyword = config.getConfig("keyword");
        log.info("钉钉IM服务初始化成功，关键词配置：{}", keyword);
    }

}
