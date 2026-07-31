package com.structure.message.plugin.im.wechatwork;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信IM服务提供商
 */
@Slf4j
@Component
public class WechatWorkImProvider implements ImProvider {

    private static final String PROVIDER_NAME = "wechatwork";
    private static final String CHANNEL_CODE = "WECHAT_WORK";
    private static final String WECHAT_WORK_API_BASE = "https://qyapi.weixin.qq.com/cgi-bin";
    private static final String ACCESS_TOKEN_URL = WECHAT_WORK_API_BASE + "/gettoken";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpClient;
    private String appId;
    private String appSecret;
    private String webhookUrl;
    private String botId;
    private String accessToken;
    private Long tokenExpireTime;

    @Override
    public ImResponse sendImMessage(ImRequest request) throws Exception {
        String messageType = request.getMessageType();
        if ("BOT".equals(messageType) || webhookUrl != null) {
            return sendBotMessage(request);
        } else {
            return sendP2PMessage(request);
        }
    }

    private ImResponse sendBotMessage(ImRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("content", "**" + request.getTitle() + "**\n\n" + request.getContent());
            body.put("msgtype", "markdown");
            body.put("markdown", markdown);
        } else {
            Map<String, Object> text = new HashMap<>();
            text.put("content", request.getContent());
            body.put("msgtype", "text");
            body.put("text", text);
        }

        String response = httpPost(webhookUrl, body);
        JSONObject jsonResponse = JSON.parseObject(response);

        if (jsonResponse.getIntValue("errcode") != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("WECHAT_WORK_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("errmsg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .rawResponse(jsonResponse)
                .build();
    }

    private ImResponse sendP2PMessage(ImRequest request) throws Exception {
        String token = getAccessToken();

        Map<String, Object> body = new HashMap<>();
        body.put("touser", request.getReceiver());
        body.put("agentid", botId);
        body.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();
        text.put("content", request.getContent());
        body.put("text", text);

        String response = httpPost(
                WECHAT_WORK_API_BASE + "/message/send?access_token=" + token,
                body
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        if (jsonResponse.getIntValue("errcode") != 0) {
            return ImResponse.builder()
                    .success(false)
                    .errorCode("WECHAT_WORK_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("errmsg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getString("msgid"))
                .rawResponse(jsonResponse)
                .build();
    }

    private String getAccessToken() throws Exception {
        if (accessToken != null && tokenExpireTime != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        String url = ACCESS_TOKEN_URL + "?corpid=" + appId + "&corpsecret=" + appSecret;
        String response = httpGet(url);
        JSONObject jsonResponse = JSON.parseObject(response);

        if (jsonResponse.getIntValue("errcode") != 0) {
            throw new MessageException("WECHAT_WORK_TOKEN_ERROR", "获取企业微信访问令牌失败：" + jsonResponse.getString("errmsg"));
        }

        accessToken = jsonResponse.getString("access_token");
        int expire = jsonResponse.getIntValue("expires_in");
        tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;

        return accessToken;
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
        log.info("企业微信IM服务已销毁");
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
        this.botId = config.getConfig("botId");
        log.info("企业微信IM服务初始化成功");
    }

}
