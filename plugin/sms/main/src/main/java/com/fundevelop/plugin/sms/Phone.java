package com.fundevelop.plugin.sms;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * .
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:31
 */
public class Phone implements Serializable {
    /** 国家区号（默认为中国） */
    private String countryCode;
    /** 电话号码 */
    private String phone;

    public Phone(String phone) {
        this.phone = phone;
    }

    public Phone(String countryCode, String phone) {
        this.countryCode = countryCode;
        this.phone = phone;
    }

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

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(getPhone())) {
            if (StringUtils.isNotBlank(getCountryCode())) {
                return getCountryCode()+getPhone();
            }

            return getPhone();
        }

        return null;
    }
}
