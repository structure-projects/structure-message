package cn.structured.message.common.exception;

import lombok.Getter;

/**
 * 消息中心异常
 */
@Getter
public class MessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误参数
     */
    private final Object[] args;

    public MessageException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public MessageException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public MessageException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public MessageException(String errorCode, String message, Throwable cause, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * 创建异常实例
     */
    public static MessageException of(String errorCode, String message) {
        return new MessageException(errorCode, message);
    }

    /**
     * 创建异常实例（带参数）
     */
    public static MessageException of(String errorCode, String message, Object... args) {
        return new MessageException(errorCode, message, args);
    }
}