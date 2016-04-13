package com.fundevelop.commons.utils;

import org.apache.commons.lang3.*;

import java.security.NoSuchAlgorithmException;

/**
 * 安全工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 22:14
 */
public class SecurityUtils {
    public static String createMd5Password(String password, String salt) throws NoSuchAlgorithmException {
        return MD5.getInstance().getMD5to32(salt + password);
    }

    public static String createToken(String key, String salt) throws NoSuchAlgorithmException {
        String originToken = key + "_" + System.currentTimeMillis() + "_" + salt;
        return MD5.getInstance().getMD5to32(originToken);
    }

    /**
     * 隐藏信息，将<code>source</code>中的字符替换成*.
     * @param source 源字符串
     * @param startPos 起始位置
     * @param length 替换字符长度
     * @return 替换后字符串
     */
    public static String hidenInfo(String source, int startPos, int length) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            source = source.trim();

            if (source.length() > startPos) {
                String pre = source.substring(0, startPos);

                if (source.length() > (startPos+length)) {
                    source = pre + StringUtils.batchCreateString("*", length) + source.substring(startPos+length);
                } else {
                    source = pre + StringUtils.batchCreateString("*", source.length()- startPos);
                }
            }
        }

        return source;
    }

    private SecurityUtils(){}
}
