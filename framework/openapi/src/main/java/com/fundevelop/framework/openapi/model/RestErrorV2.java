package com.fundevelop.framework.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fundevelop.framework.openapi.exception.RestException;

/**
 * Rest响应错误对象.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/10 16:39
 */
public class RestErrorV2 extends RestError {
    public RestErrorV2(RestException re) {
        super(re);

        if (re.getSubCode() != null) {
            setErrorCode(re.getSubCode());
        }
    }

    public RestErrorV2(int errorCode, Object errorInfo) {
        super(errorCode, errorInfo);
    }

    @Override
    @JsonProperty("code")
    public int getErrorCode() {
        return super.getErrorCode();
    }

    @Override
    @JsonProperty("info")
    public Object getErrorInfo() {
        return super.getErrorInfo();
    }
}
