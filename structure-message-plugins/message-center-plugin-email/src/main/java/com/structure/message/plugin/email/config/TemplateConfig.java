package com.structure.message.plugin.email.config;

import lombok.Data;

@Data
public class TemplateConfig {

    private boolean enabled = true;
    private String defaultTemplate = "default";
    private String encoding = "UTF-8";
    private boolean cache = true;
    private int cacheSize = 100;
    private int updateDelay = 3600;
}
