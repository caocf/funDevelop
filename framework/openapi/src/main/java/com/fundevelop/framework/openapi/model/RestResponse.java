package com.fundevelop.framework.openapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Restful Cgi 响应对象..
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 18:19
 */
public class RestResponse {
    private String messageId; // 客户端请求时发送的消息ID
    private String requestId; // 请求ID
    private RestError error;
    private int statusCode = 0; // 响应代码
    private String cmd; // 操作命令
    private Object response; // 返回给客户端的结果
    private Date responseTime; // 响应时间
    private long responseTimestamp; // 响应时的时间戳
    private long duration; // 耗时
    private Object debugInfo; // 调试信息
    private String clientIp;
    private boolean usedCache = false; // 是否使用了缓存的结果
    private Map<String, Object> extraInfo = new HashMap<>(); // 附加信息
    private Map<String, Object> auth = new HashMap<>(); // 身份认证信息

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RestError getError() {
        return error;
    }

    public void setError(RestError error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
        if (responseTime != null) {
            setResponseTimestamp(responseTime.getTime());
        }
    }

    public long getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Object getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(Object debugInfo) {
        this.debugInfo = debugInfo;
    }

    public boolean isUsedCache() {
        return usedCache;
    }

    public void setUsedCache(boolean usedCache) {
        this.usedCache = usedCache;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public Map<String, Object> getAuth() {
        return auth;
    }
}
