package com.fundevelop.plugin.sms;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 短息发送回执Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 15:50
 */
public class SmsReport implements Serializable {
    /** 通道信息ID */
    private String msgId;
    /** 特服号 */
    private String serviceCode;
    /** 响应发送状态 */
    private String status;
    /** 响应时间 */
    private Timestamp responseTime;
    /** 通道代码 */
    private String channelCode;

    public SmsReport(String msgId, String serviceCode, String status, Timestamp responseTime, String channelCode) {
        this.msgId = msgId;
        this.serviceCode = serviceCode;
        this.status = status;
        this.responseTime = responseTime;
        this.channelCode = channelCode;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Timestamp responseTime) {
        this.responseTime = responseTime;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
}
