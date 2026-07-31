package com.structure.message.plugin.im.feishu;

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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 飞书IM服务提供商
 */
@Slf4j
@Component
public class FeishuImProvider implements ImProvider {

    private static final String PROVIDER_NAME = "feishu";
    private static final String CHANNEL_CODE = "FEISHU";
    private static final String FEISHU_API_BASE = "https://open.feishu.cn";
    private static final String TENANT_ACCESS_TOKEN_URL = FEISHU_API_BASE + "/open-apis/auth/v3/tenant_access_token/internal";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpClient;
    private String appId;
    private String appSecret;
    private String webhookUrl;
    private String signSecret;
    private String botId;
    private String accessToken;
    private Long tokenExpireTime;

    @Override
    public ImResponse sendImMessage(ImRequest request) throws Exception {
        String messageType = request.getMessageType();
        String receiver = request.getReceiver();
        
        // 如果明确指定为P2P，走点对点消息路径
        if ("P2P".equals(messageType)) {
            return sendP2PMessage(request);
        }
        // 优先判断：如果有chat_id或者receiver以oc_开头，用应用机器人发群聊
        if ("BOT".equals(messageType) && receiver != null && receiver.startsWith("oc_")) {
            return sendAppBotToChat(request);
        }
        // 如果有webhook，用自定义机器人
        else if ("BOT".equals(messageType) || webhookUrl != null) {
            return sendBotMessage(request);
        } 
        // 其他情况发P2P
        else {
            return sendP2PMessage(request);
        }
    }
    
    private ImResponse sendAppBotToChat(ImRequest request) throws Exception {
        String token = getAccessToken();
        
        // 如果有用户access token，优先使用用户token
        if (request.getExtra() != null && request.getExtra().get("userAccessToken") != null) {
            token = request.getExtra().get("userAccessToken").toString();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("receive_id", request.getReceiver());

        // 判断是否需要@人
        if (request.getParams() != null && request.getParams().get("atUserIds") != null) {
            // 使用富文本消息，正确的@格式
            body.put("msg_type", "post");
            
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> zhCn = new HashMap<>();
            zhCn.put("title", request.getTitle() != null ? request.getTitle() : "通知");
            
            // 构建消息内容
            java.util.List<Object> elements = new java.util.ArrayList<>();
            
            // 添加文本段
            Map<String, Object> textElement = new HashMap<>();
            textElement.put("tag", "text");
            textElement.put("text", request.getContent() + " ");
            elements.add(textElement);
            
            // 添加@人
            String atUserIdsStr = request.getParams().get("atUserIds").toString();
            String[] atUserIds = atUserIdsStr.split(",");
            for (String atUserId : atUserIds) {
                atUserId = atUserId.trim();
                if (!atUserId.isEmpty()) {
                    Map<String, Object> atElement = new HashMap<>();
                    atElement.put("tag", "at");
                    atElement.put("user_id", atUserId);
                    atElement.put("user_name", "chuck");
                    elements.add(atElement);
                }
            }
            
            // 富文本格式
            java.util.List<java.util.List<Object>> contentList = new java.util.ArrayList<>();
            contentList.add(elements);
            zhCn.put("content", contentList);
            content.put("zh_cn", zhCn);
            body.put("content", JSON.toJSONString(content));
        } else {
            // 普通文本消息
            body.put("msg_type", "text");
            Map<String, Object> content = new HashMap<>();
            content.put("text", request.getContent());
            body.put("content", JSON.toJSONString(content));
        }

        String response = httpPostWithToken(
                FEISHU_API_BASE + "/open-apis/im/v1/messages?receive_id_type=chat_id",
                body,
                token
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        if (jsonResponse.getIntValue("code") != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("FEISHU_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("msg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getJSONObject("data").getString("message_id"))
                .rawResponse(jsonResponse)
                .build();
    }

    private ImResponse sendBotMessage(ImRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            Map<String, Object> content = new HashMap<>();
            content.put("title", request.getTitle());
            content.put("text", request.getContent());
            body.put("msg_type", "post");
            Map<String, Object> zhCnContent = new HashMap<>();
            zhCnContent.put("zh_cn", content);
            body.put("content", JSON.toJSONString(zhCnContent));
        } else {
            Map<String, Object> content = new HashMap<>();
            content.put("text", request.getContent());
            body.put("msg_type", "text");
            body.put("content", JSON.toJSONString(content));
        }

        if (signSecret != null && !signSecret.isEmpty()) {
            long timestamp = System.currentTimeMillis() / 1000;
            String sign = generateSign(timestamp, signSecret);
            body.put("timestamp", timestamp);
            body.put("sign", sign);
        }

        String url = webhookUrl != null ? webhookUrl : FEISHU_API_BASE + "/open-apis/bot/v2/hook/" + botId;
        String response = httpPost(url, body);
        JSONObject jsonResponse = JSON.parseObject(response);

        if (jsonResponse.getIntValue("code") != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("FEISHU_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("msg"))
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
        
        // 如果有用户access token，优先使用用户token
        if (request.getExtra() != null && request.getExtra().get("userAccessToken") != null) {
            token = request.getExtra().get("userAccessToken").toString();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("receive_id", request.getReceiver());
        body.put("msg_type", "text");

        Map<String, Object> content = new HashMap<>();
        content.put("text", request.getContent());
        body.put("content", JSON.toJSONString(content));

        // 自动识别ID类型，支持open_id和user_id
        String receiveIdType = request.getReceiver().startsWith("ou_") ? "open_id" : "user_id";
        String url = FEISHU_API_BASE + "/open-apis/im/v1/messages?receive_id_type=" + receiveIdType;

        String response = httpPostWithToken(
                url,
                body,
                token
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        if (jsonResponse.getIntValue("code") != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("FEISHU_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("msg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getJSONObject("data").getString("message_id"))
                .rawResponse(jsonResponse)
                .build();
    }

    private String getAccessToken() throws Exception {
        if (accessToken != null && tokenExpireTime != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("app_secret", appSecret);

        String response = httpPost(TENANT_ACCESS_TOKEN_URL, body);
        JSONObject jsonResponse = JSON.parseObject(response);

        if (jsonResponse.getIntValue("code") != 0) {
            throw new MessageException("FEISHU_TOKEN_ERROR", "获取飞书访问令牌失败：" + jsonResponse.getString("msg"));
        }

        accessToken = jsonResponse.getString("tenant_access_token");
        int expire = jsonResponse.getIntValue("expire");
        tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;

        return accessToken;
    }

    private String generateSign(long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(new byte[]{});
        return Base64.getEncoder().encodeToString(signData);
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

    private String httpPostWithToken(String url, Object body, String token) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(JSON.toJSONString(body), JSON_MEDIA_TYPE))
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
        log.info("飞书IM服务已销毁");
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
        log.info("飞书IM服务初始化成功");
    }

}
