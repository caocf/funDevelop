package com.fundevelop.framework.openapi.model;

/**
 * Rest响应错误对象
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 19:16
 */
public class RestError {
    private int errorCode;
    private Object errorInfo;

    public RestError() {}

    public RestError(Object errorInfo) {
        this.errorInfo = errorInfo;
    }

    public RestError(int errorCode, Object errorInfo) {
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(Object errorInfo) {
        this.errorInfo = errorInfo;
    }
}
