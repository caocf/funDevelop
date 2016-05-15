package com.fundevelop.framework.manager.jpa.query.dynamic;

/**
 * 动态构建SQL查询异常.
 * <p>描述:主要是程序员代码导致的异常</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:33
 */
public class DynamicQueryException extends RuntimeException {
    public DynamicQueryException() {
        super();
    }

    /**
     * 构造函数.
     * @param message 异常信息
     * @param cause 异常
     */
    public DynamicQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数.
     * @param message 异常信息
     */
    public DynamicQueryException(String message) {
        super(message);
    }

    /**
     * 构造函数.
     * @param cause 异常
     */
    public DynamicQueryException(Throwable cause) {
        super(cause);
    }
}
