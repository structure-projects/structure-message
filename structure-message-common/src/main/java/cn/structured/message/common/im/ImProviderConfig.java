package cn.structured.message.common.im;

import lombok.Data;

/**
 * IM提供商配置
 */
@Data
public class ImProviderConfig {

    /**
     * 提供商代码
     */
    private String providerCode;

    /**
     * 提供商名称
     */
    private String name;

}
