package cn.structured.message.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgChannelConfigVO {

    private Long id;

    private Long orgId;

    private Long channelId;

    private String configName;

    private String configValue;

    private Integer status;

    private Integer isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}