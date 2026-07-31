package cn.structured.message.common.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * IM消息请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImRequest {

    /**
     * 接收者
     */
    private String receiver;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 扩展参数
     */
    private Map<String, Object> extra;

    /**
     * 参数（兼容插件参数
     */
    private Map<String, Object> params;

}
