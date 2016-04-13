package com.fundevelop.framework.openapi.model;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础请求对象.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/15 10:12
 */
public class BaseRestRequest {
    protected RestRequest restRequest;
    protected HttpServletRequest httpRequest;

    public RestRequest getRestRequest() {
        return restRequest;
    }

    public void setRestRequest(RestRequest restRequest) {
        this.restRequest = restRequest;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
}
