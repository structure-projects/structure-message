package com.structure.message.plugin.im.netease;

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

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 网易云信IM服务提供商
 */
@Slf4j
@Component
public class NeteaseImProvider implements ImProvider {

    private static final String PROVIDER_NAME = "netease";
    private static final String CHANNEL_CODE = "NETEASE_IM";
    private static final String NETEASE_API_BASE = "https://api.netease.im/nimserver";

    private OkHttpClient httpClient;
    private String appId;
    private String appSecret;
    private String botId;

    @Override
    public ImResponse sendImMessage(ImRequest request) throws Exception {
        String nonce = String.valueOf(System.currentTimeMillis());
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        String checkSum = generateCheckSum(appSecret, nonce, curTime);

        Map<String, Object> body = new HashMap<>();
        body.put("from", botId != null ? botId : "admin");
        body.put("ope", 0);
        body.put("to", request.getReceiver());
        body.put("type", 0);
        body.put("body", request.getContent());

        StringBuilder formBody = new StringBuilder();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            if (formBody.length() > 0) {
                formBody.append("&");
            }
            formBody.append(entry.getKey())
                    .append("=")
                    .append(java.net.URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }

        Request req = new Request.Builder()
                .url(NETEASE_API_BASE + "/msg/sendMsg.action")
                .addHeader("AppKey", appId)
                .addHeader("Nonce", nonce)
                .addHeader("CurTime", curTime)
                .addHeader("CheckSum", checkSum)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .post(RequestBody.create(formBody.toString(), MediaType.parse("application/x-www-form-urlencoded;charset=utf-8")))
                .build();

        try (Response response = httpClient.newCall(req).execute()) {
            if (response.body() == null) {
                throw new MessageException("NETEASE_IM_ERROR", "响应为空");
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);

            if (jsonResponse.getIntValue("code") != 200) {
                return ImResponse.builder()
                        .success(false)
                        .errorCode("NETEASE_IM_SEND_ERROR")
                        .errorMessage(jsonResponse.getString("desc"))
                        .rawResponse(jsonResponse)
                        .build();
            }

            return ImResponse.builder()
                    .success(true)
                    .messageId(jsonResponse.getJSONObject("data").getString("msgId"))
                    .rawResponse(jsonResponse)
                    .build();
        }
    }

    private String generateCheckSum(String appSecret, String nonce, String curTime) throws Exception {
        String str = appSecret + nonce + curTime;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(str.getBytes("UTF-8"));
        return bytesToHex(digest);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
        log.info("网易云信IM服务已销毁");
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
        this.botId = config.getConfig("botId");
        log.info("网易云信IM服务初始化成功");
    }

}
