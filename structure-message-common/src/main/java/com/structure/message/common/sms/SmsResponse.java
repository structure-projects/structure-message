package com.structure.message.common.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponse {
    private boolean success;
    private String messageId;
    private String errorCode;
    private String errorMessage;

}
