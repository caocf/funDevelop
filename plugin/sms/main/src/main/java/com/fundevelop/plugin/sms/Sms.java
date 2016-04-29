package com.fundevelop.plugin.sms;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短信信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:26
 */
public class Sms implements Serializable {
    /** 短信ID */
    private String smsId;
    /** 手机号 */
    private List<Phone> phones;
    /** 短信内容 */
    private String content;
    /** 发送时间（用于定时发送，不是所有平台都支持） */
    private Date sendTiming;

    /** 业务系统名称或代码 */
    private String system;
    /** 业务系统模块名称或代码 */
    private String module;

    public Sms(String smsId, List<Phone> phones, String content) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
    }

    public Sms(String smsId, List<Phone> phones, String content, Date sendTiming) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
        this.sendTiming = sendTiming;
    }

    public Sms(String smsId, List<Phone> phones, String content, String system) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
        this.system = system;
    }

    public Sms(String smsId, List<Phone> phones, String content, Date sendTiming, String system) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
        this.sendTiming = sendTiming;
        this.system = system;
    }

    public Sms(String smsId, List<Phone> phones, String content, String system, String module) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
        this.system = system;
        this.module = module;
    }

    public Sms(String smsId, List<Phone> phones, String content, Date sendTiming, String system, String module) {
        this.smsId = smsId;
        this.phones = phones;
        this.content = content;
        this.sendTiming = sendTiming;
        this.system = system;
        this.module = module;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
