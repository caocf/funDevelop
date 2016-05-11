package com.fundevelop.plugin.sns.weixin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 获取微信（公众号）Access_token响应信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/8 23:39
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CgiAccessToken implements Serializable {
    /** 获取到的凭证 */
    private String access_token;
    /** 凭证有效时间，单位：秒 */
    private long expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
