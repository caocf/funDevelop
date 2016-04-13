package com.fundevelop.framework.openapi.utils;

import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;

/**
 * OpenAPI cmd服务代理接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/18 19:59
 */
public interface CmdServiceInvoker {
    /**
     * 调用cmd服务
     */
    public void invoke(RestRequest request, RestResponse response);
}
