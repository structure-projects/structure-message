package com.structure.message.plugin.email.config;

import lombok.Data;

@Data
public class SecurityConfig {

    private boolean validateRecipients = true;
    private boolean checkDomainBlacklist = true;
    private String[] domainBlacklist = {"tempmail.com", "10minutemail.com"};
    private int maxRecipients = 50;
    private boolean enableAntiSpam = true;
}
