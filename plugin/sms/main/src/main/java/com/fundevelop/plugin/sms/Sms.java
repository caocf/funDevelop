package com.fundevelop.plugin.sms;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 短信信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:26
 */
public class Sms implements Serializable {
    /** 手机号 */
    private List<Phone> phones;
    /** 短信内容 */
    private String content;
    /** 发送时间（用于定时发送，不是所有平台都支持） */
    private Date sendTiming;

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
}
