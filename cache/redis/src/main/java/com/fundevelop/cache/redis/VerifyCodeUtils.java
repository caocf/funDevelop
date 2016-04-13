package com.fundevelop.cache.redis;

import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 验证码工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 11:24
 */
public class VerifyCodeUtils {
    /**
     * 获取验证码.
     */
    public static String getVerifyCode(String mobilePhone, String codeType) {
        return getVerifyCode(mobilePhone, codeType, false, 0);
    }

    /**
     * 让验证码立即失效.
     */
    public static void expireVerifyCode(String mobilePhone, String codeType) {
        try {
            String key = getVerifyCodeKey(mobilePhone, codeType);
            RedisUtil.del(key);
        } catch (Exception e) {
            logger.error("缓存调用失败:", e);
        }
    }

    /**
     * 获取已有验证码（未失效），如果没有创建并返回
     */
    public static String getVerifyCode(String mobilePhone, String codeType, boolean createWhenNoExsit, int expireSeconds) {
        String verifyCode = null;

        try {
            String key = getVerifyCodeKey(mobilePhone, codeType);
            verifyCode = RedisUtil.get(key);

            if (createWhenNoExsit && StringUtils.isBlank(verifyCode)) {
                verifyCode = String.valueOf(RandomStringUtils.randomNumeric(Integer.parseInt(PropertyUtil.get("verifycode.length", "4"),10)));
                RedisUtil.set(key, verifyCode, expireSeconds);
            }
        } catch (Exception e) {
            logger.error("缓存调用失败:", e);
        }

        return verifyCode;
    }

    private static String getVerifyCodeKey(String mobilePhone, String codeType) {
        return "VERIFY_CODE:" + mobilePhone + ":" + codeType;
    }

    private static Logger logger = LoggerFactory.getLogger(VerifyCodeUtils.class);

    private VerifyCodeUtils(){}
}
