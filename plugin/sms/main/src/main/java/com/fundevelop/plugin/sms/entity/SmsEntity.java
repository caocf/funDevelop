package com.fundevelop.plugin.sms.entity;


import com.fundevelop.persistence.entity.hibernate.LongIDEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 短信信息实体类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 21:27
 */
@Entity
@Table(name = "fun_sms")
public class SmsEntity extends LongIDEntity {
    /** 国家代码 */
    private String countryCode = "86";
    /** 手机号码 */
    private String phone;
    /** 短信内容 */
    private String content;
    /** 优先级 */
    private int priority;
    /** 定时发送时间 */
    private Date sendTime;
    /** 业务系统 */
    private String system;
    /** 业务模块 */
    private String module;
    /** 备注 */
    private String memo;
    /** 登记日期 */
    private Date createTime;
    /** 实际发送时间 */
    private Date sendTiming;
    /** 发送状态(1:未发送，2:已发送) */
    private String status;
    /** RRID */
    private String rrid;
    /** 特服号 */
    private String serviceCode;
    /** 响应发送状态 */
    private String responseStatus;
    /** 响应时间 */
    private Date responseTime;
    /** 短信通道 */
    private String channel;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getSendTiming() {
        return sendTiming;
    }

    public void setSendTiming(Date sendTiming) {
        this.sendTiming = sendTiming;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRrid() {
        return rrid;
    }

    public void setRrid(String rrid) {
        this.rrid = rrid;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
