package com.fundevelop.framework.erp.audit;

import java.io.Serializable;
import java.util.Date;

/**
 * ERP系统操作日志.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/6 18:51
 */
public class ErpOperationLog implements Serializable {
    /** 模块名称. */
    private String modleName;
    /** 动作类型. */
    private String action;
    /** 动作内容. */
    private String actionContent;
    /** 动作参数. */
    private String actionData;
    /** 操作时间. */
    private Date actionTime;
    /** 操作人ID. */
    private String userId;
    /** 操作人名称. */
    private String userName;
    /** IP地址. */
    private String ip;
    /** URL地址. */
    private String url;
    /** 操作结果. */
    private String result;
    /** 处理时间. */
    private Long timeConsuming;
    /** 异常信息. */
    private String exception;

    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionContent() {
        return actionContent;
    }

    public void setActionContent(String actionContent) {
        this.actionContent = actionContent;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(Long timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
