package com.fundevelop.framework.erp.exception;

/**
 * 致命严重异常.
 * <p>描述:主要是程序员代码导致的异常</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/6 9:38
 */
public class FatalException extends RuntimeException {
    /**
     * 构造函数.
     */
    public FatalException() {
        super();
    }

    /**
     * 构造函数.
     * @param message 异常信息
     * @param cause 异常
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数.
     * @param message 异常信息
     */
    public FatalException(String message) {
        super(message);
    }

    /**
     * 构造函数.
     * @param cause 异常
     */
    public FatalException(Throwable cause) {
        super(cause);
    }
}
