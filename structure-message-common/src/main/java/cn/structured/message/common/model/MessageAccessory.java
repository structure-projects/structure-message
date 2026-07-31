package cn.structured.message.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 消息附件
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2021/7/5 14:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAccessory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 附件ID
     */
    private Long accessoryId;

    /**
     * 资源类型
     */
    private Integer resourceType;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源图标
     */
    private String resourceIcon;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 描述
     */
    private String resourceDesc;

    /**
     * 数量
     */
    private Long amount;

    /**
     * 状态
     */
    private Integer state;
}