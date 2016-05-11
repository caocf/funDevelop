package com.fundevelop.framework.openapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Restful Cgi 请求对象..
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 18:20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestRequest {
    private String callback; // jsonp模式支持js回调函数名称
    private String messageId; // 客户端请求时发送的消息ID
    private String requestId; // UUID策略生成的唯一ID
    private String cmd; // 操作命令
    private Map<String, Object> parameters = new HashMap<>(); // 请求参数
    private Map<String, Object> replaceParams = new HashMap<>(); // 使用静态资源时替换参数
    private String token; // 用户的token
    private String appPushToken; //用于推送消息的ID
    private Integer cityId; // 城市ID
    private String resolution; // 分辨率
    private String locationXy; // 经纬度
    private Map<String, Object> deviceInfo = new HashMap<>(); // 设备型号
    private String appVersion; // APP版本
    private Date requestTime; // 请求时间
    private String ip; // 客户端IP
    private boolean debug = false; // 是否开启debug
    private boolean enableStaticJson = false; // 是否开启静态JSON文件支持
    private String clientIdentifierCode; // 客户端唯一标示代码
    private String runningMode = "normal"; // 请求模式：normal、ping
    private String appDomain = ""; // APP域，区分不同应用的APP

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

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

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getLocationXy() {
        return locationXy;
    }

    public void setLocationXy(String locationXy) {
        this.locationXy = locationXy;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Map<String, Object> getReplaceParams() {
        return replaceParams;
    }

    public void addReplaceParams(String key, Object value) {
        replaceParams.put(key, value);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, Object> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isEnableStaticJson() {
        return enableStaticJson;
    }

    public void setEnableStaticJson(boolean enableStaticJson) {
        this.enableStaticJson = enableStaticJson;
    }

    public String getClientIdentifierCode() {
        return clientIdentifierCode;
    }

    public void setClientIdentifierCode(String clientIdentifierCode) {
        this.clientIdentifierCode = clientIdentifierCode;
    }

    public String getRunningMode() {
        return runningMode;
    }

    public void setRunningMode(String runningMode) {
        this.runningMode = runningMode;
    }

    public String getAppDomain() {
        return appDomain;
    }

    public void setAppDomain(String appDomain) {
        this.appDomain = appDomain;
    }

    public String getAppPushToken() {
        return appPushToken;
    }

    public void setAppPushToken(String appPushToken) {
        this.appPushToken = appPushToken;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageId", messageId)
                .append("requestId", requestId)
                .append("cmd", cmd)
                .append("parameters", parameters)
                .append("token", token)
                .append("appPushToken", appPushToken)
                .append("cityId", cityId)
                .append("resolution", resolution)
                .append("locationXy", locationXy)
                .append("deviceInfo", deviceInfo)
                .append("appVersion", appVersion)
                .append("requestTime", requestTime)
                .append("ip", ip)
                .append("debug", debug)
                .append("enableStaticJson", enableStaticJson)
                .append("clientIdentifierCode", clientIdentifierCode)
                .append("runningMode", runningMode)
                .append("appDomain", appDomain)
                .toString();
    }
}
