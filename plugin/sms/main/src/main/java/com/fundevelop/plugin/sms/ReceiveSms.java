package com.fundevelop.plugin.sms;

import java.io.Serializable;

/**
 * 通道上行短信Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 16:00
 */
public class ReceiveSms implements Serializable {
    /** 手机号 */
    private String phone;
    /** 短信内容 */
    private String content;
    /** 发送时间 */
    private String delivertime;

    public ReceiveSms(String phone, String content, String delivertime) {
        this.phone = phone;
        this.content = content;
        this.delivertime = delivertime;
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

    public String getDelivertime() {
        return delivertime;
    }

    public void setDelivertime(String delivertime) {
        this.delivertime = delivertime;
    }
}
