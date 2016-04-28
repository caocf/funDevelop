package com.fundevelop.plugin.sms;

import java.io.Serializable;

/**
 * .
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:31
 */
public class Phone implements Serializable {
    /** 国家区号（默认为中国） */
    private String countryCode = "86";
    /** 电话号码 */
    private String phone;

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
}
