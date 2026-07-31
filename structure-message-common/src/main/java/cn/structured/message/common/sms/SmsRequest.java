package cn.structured.message.common.sms;

import lombok.Data;

@Data
public class SmsRequest {
    private String phoneNumber;
    private String content;
    private String templateCode;
    private Object templateParams;
    private String signName;
    private String businessId;
}
