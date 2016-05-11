package com.fundevelop.plugin.sns.weixin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 获取微信（公众号）jsapi_ticket响应信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/9 22:24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket implements Serializable {
    /** 获取到的凭证 */
    private String ticket;
    /** 凭证有效时间，单位：秒 */
    private long expires_in;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
