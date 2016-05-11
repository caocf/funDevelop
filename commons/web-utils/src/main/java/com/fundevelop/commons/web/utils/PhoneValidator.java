package com.fundevelop.commons.web.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码验证工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/10 22:26
 */
public class PhoneValidator {
    /**
     * 验证手机号码的合法性.
     */
    public static boolean valid(String phone) {
        return valid("86", phone);
    }

    /**
     * 验证手机号码的合法性.
     * @param countryCode 国家代码
     * @param phone 手机号码
     */
    public static boolean valid(String countryCode, String phone) {
        String regex = PropertyUtil.get("phone.validate.regex."+countryCode);

        if (StringUtils.isBlank(regex)) {
            regex = PropertyUtil.get("phone.validate.regex.default");

            if (StringUtils.isBlank(regex)) {
                if (com.fundevelop.commons.utils.StringUtils.isBooleanTrue(PropertyUtil.get("phone.validate.defaultPass", "false"))) {
                    return true;
                }

                throw new RuntimeException("没有配置国家代码为【"+countryCode+"】的验证规则");
            }
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private PhoneValidator(){}
}
