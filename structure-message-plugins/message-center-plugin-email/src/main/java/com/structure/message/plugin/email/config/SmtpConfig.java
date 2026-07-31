package com.structure.message.plugin.email.config;

import lombok.Data;

@Data
public class SmtpConfig {

    private String host;
    private int port = 587;
    private String username;
    private String password;
    private String auth = "true";
    private String starttls = "true";
    private String connectiontimeout = "5000";
    private String timeout = "10000";
    private String writetimeout = "10000";
}
