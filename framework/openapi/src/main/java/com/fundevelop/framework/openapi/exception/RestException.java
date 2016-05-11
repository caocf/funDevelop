package com.fundevelop.framework.openapi.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OpenAPI 统一异常.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/21 8:24
 */
@JsonIgnoreProperties(value = { "cause", "stackTrace"})
public class RestException extends RuntimeException {
    private int errorCode;
    private Integer subCode;
    private Object errorInfo;

    public RestException(int errorCode, Object errorInfo) {
        super(errorInfo==null?"":errorInfo.toString());
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public RestException(int errorCode, int subCode, Object errorInfo) {
        super(errorInfo==null?"":errorInfo.toString());
        this.errorCode = errorCode;
        this.subCode = subCode;
        this.errorInfo = errorInfo;
    }

    public RestException(Throwable cause) {
        super(cause);
        this.errorCode = 500;
        this.errorInfo = cause.getMessage();
    }

    public RestException(Object errorInfo, Throwable cause) {
        super(errorInfo==null?"":errorInfo.toString(), cause);
        this.errorCode = 500;
        this.errorInfo = errorInfo;
    }

    public RestException(int errorCode, Object errorInfo, Throwable cause) {
        super(errorInfo==null?"":errorInfo.toString(), cause);
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public RestException(int errorCode,int subCode, Object errorInfo, Throwable cause) {
        super(errorInfo==null?"":errorInfo.toString(), cause);
        this.errorCode = errorCode;
        this.subCode = subCode;
        this.errorInfo = errorInfo;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Object getErrorInfo() {
        return errorInfo;
    }

    public Integer getSubCode() {
        return subCode;
    }
}
