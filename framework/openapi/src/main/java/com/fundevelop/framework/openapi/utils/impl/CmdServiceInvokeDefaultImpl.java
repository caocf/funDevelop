package com.fundevelop.framework.openapi.utils.impl;

import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import com.fundevelop.framework.openapi.utils.CmdServiceInvoker;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * OpenAPI cmd服务代理接口默认实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/21 18:46
 */
public class CmdServiceInvokeDefaultImpl implements CmdServiceInvoker {
    @Override
    public void invoke(RestRequest request, RestResponse response) {
        Object responseBody = null;
        try {
            responseBody = ReflectionUtils.invokeMethod(method, bean, methodParameters);
        } finally {
            response.setResponse(responseBody);
            Date responseTime = new Date();
            response.setResponseTime(responseTime);
            response.setDuration(responseTime.getTime() - request.getRequestTime().getTime());
        }
    }

    public CmdServiceInvokeDefaultImpl(Object bean, Method method, Object[] methodParameters) {
        this.bean = bean;
        this.method = method;
        this.methodParameters = methodParameters;
    }

    private Object bean;
    private Method method;
    private Object[] methodParameters;
}
