package com.structure.message.plugin.im.wechat;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import cn.structured.message.common.exception.MessageException;
import cn.structured.message.common.im.ImProvider;
import cn.structured.message.common.im.ImRequest;
import cn.structured.message.common.im.ImResponse;
import cn.structured.message.common.plugin.MessageChannelConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WechatImProvider implements ImProvider {

    private static final String PROVIDER_NAME = "wechat";
    private static final String CHANNEL_CODE = "WECHAT";
    private static final String WECHAT_API_BASE = "https://api.weixin.qq.com/cgi-bin";
    private static final String ACCESS_TOKEN_URL = WECHAT_API_BASE + "/token";
    private static final String TEMPLATE_MESSAGE_URL = WECHAT_API_BASE + "/message/template/send";
    private static final String SUBSCRIBE_MESSAGE_URL = WECHAT_API_BASE + "/message/subscribe/send";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpClient;
    private String appId;
    private String appSecret;
    private String templateId;
    private String accessToken;
    private Long tokenExpireTime;

    @Override
    public ImResponse sendImMessage(ImRequest request) throws Exception {
        String messageType = request.getMessageType();
        
        if ("SUBSCRIBE".equals(messageType)) {
            return sendSubscribeMessage(request);
        } else {
            return sendTemplateMessage(request);
        }
    }

    private ImResponse sendTemplateMessage(ImRequest request) throws Exception {
        String token = getAccessToken();

        Map<String, Object> body = new HashMap<>();
        body.put("touser", request.getReceiver());
        body.put("template_id", getTemplateId(request));
        
        if (request.getUrl() != null && !request.getUrl().isEmpty()) {
            body.put("url", request.getUrl());
        }

        Map<String, Object> data = new HashMap<>();
        
        Map<String, Object> first = new HashMap<>();
        first.put("value", request.getTitle() != null ? request.getTitle() : "通知");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", request.getContent());
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", "");
        remark.put("color", "#173177");
        data.put("remark", remark);

        if (request.getExtra() != null) {
            for (Map.Entry<String, Object> entry : request.getExtra().entrySet()) {
                if (entry.getKey().startsWith("keyword") || entry.getKey().equals("first") || entry.getKey().equals("remark")) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("value", entry.getValue() != null ? entry.getValue().toString() : "");
                    item.put("color", "#173177");
                    data.put(entry.getKey(), item);
                }
            }
        }

        body.put("data", data);

        String response = httpPost(
                TEMPLATE_MESSAGE_URL + "?access_token=" + token,
                body
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        int errcode = jsonResponse.getIntValue("errcode");
        
        if (errcode != 0) {
            log.error("微信公众号模板消息发送失败，errcode={}, errmsg={}", errcode, jsonResponse.getString("errmsg"));
            return ImResponse.builder()
                    .success(false)
                    .errorCode("WECHAT_TEMPLATE_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("errmsg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        log.info("微信公众号模板消息发送成功，msgid={}", jsonResponse.getString("msgid"));
        return ImResponse.builder()
                .success(true)
                .messageId(jsonResponse.getString("msgid"))
                .rawResponse(jsonResponse)
                .build();
    }

    private ImResponse sendSubscribeMessage(ImRequest request) throws Exception {
        String token = getAccessToken();

        Map<String, Object> body = new HashMap<>();
        body.put("touser", request.getReceiver());
        body.put("template_id", getTemplateId(request));
        
        if (request.getUrl() != null && !request.getUrl().isEmpty()) {
            body.put("page", request.getUrl());
        }

        Map<String, Object> data = new HashMap<>();

        if (request.getExtra() != null) {
            for (Map.Entry<String, Object> entry : request.getExtra().entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("value", entry.getValue() != null ? entry.getValue().toString() : "");
                data.put(entry.getKey(), item);
            }
        } else {
            Map<String, Object> thing1 = new HashMap<>();
            thing1.put("value", request.getTitle() != null ? request.getTitle() : "通知");
            data.put("thing1", thing1);

            Map<String, Object> thing2 = new HashMap<>();
            thing2.put("value", request.getContent());
            data.put("thing2", thing2);
        }

        body.put("data", data);

        String response = httpPost(
                SUBSCRIBE_MESSAGE_URL + "?access_token=" + token,
                body
        );

        JSONObject jsonResponse = JSON.parseObject(response);
        int errcode = jsonResponse.getIntValue("errcode");
        
        if (errcode != 0) {
            log.error("微信小程序订阅消息发送失败，errcode={}, errmsg={}", errcode, jsonResponse.getString("errmsg"));
            return ImResponse.builder()
                    .success(false)
                    .errorCode("WECHAT_SUBSCRIBE_SEND_ERROR")
                    .errorMessage(jsonResponse.getString("errmsg"))
                    .rawResponse(jsonResponse)
                    .build();
        }

        log.info("微信小程序订阅消息发送成功");
        return ImResponse.builder()
                .success(true)
                .rawResponse(jsonResponse)
                .build();
    }

    private String getTemplateId(ImRequest request) {
        if (request.getExtra() != null && request.getExtra().get("templateId") != null) {
            return request.getExtra().get("templateId").toString();
        }
        return templateId;
    }

    private String getAccessToken() throws Exception {
        if (accessToken != null && tokenExpireTime != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        String url = ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        String response = httpGet(url);
        JSONObject jsonResponse = JSON.parseObject(response);

        int errcode = jsonResponse.getIntValue("errcode");
        if (errcode != 0) {
            throw new MessageException("WECHAT_TOKEN_ERROR", "获取微信访问令牌失败：" + jsonResponse.getString("errmsg"));
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
        log.info("微信IM服务已销毁");
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
        this.templateId = config.getConfig("templateId");
        log.info("微信IM服务初始化成功");
    }

}