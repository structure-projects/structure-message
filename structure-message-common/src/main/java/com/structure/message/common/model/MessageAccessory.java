package com.structure.message.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAccessory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long messageId;

    private Long accessoryId;

    private Integer resourceType;

    private String resourceId;

    private String resourceName;

    private String resourceIcon;

    private String resourceCode;

    private String resourceDesc;

    private Long amount;

    private Integer state;
}