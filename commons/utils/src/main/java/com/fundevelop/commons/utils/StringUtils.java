package com.fundevelop.commons.utils;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * 字符串工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 11:37
 */
public class StringUtils {
    /**
     * 格式化价格.
     */
    public static String formatPrice(Double price) {
        return formatPrice(price, "###,###.##");
    }

    /**
     * 格式化价格.
     */
    public static String formatPrice(String price, String pattern) {
        price = null2Empty(price);

        if (org.apache.commons.lang3.StringUtils.isNumeric(price)) {
            price = new DecimalFormat(pattern).format(Double.parseDouble(price));
        } else if (!org.apache.commons.lang3.StringUtils.isEmpty(price)) {
            String tmp = price;

            try {
                tmp = new DecimalFormat(pattern).format(Double.parseDouble(price));
                price = tmp;
            } catch (Exception ex) {}
        }

        return price;
    }

    /**
     * 格式化价格.
     */
    public static String formatPrice(Double price, String pattern) {
        if(price != null) {
            return new DecimalFormat(pattern).format(price);
        }

        return "";
    }

    /**
     * 如果传入字符串为null则输出空，否则原样返回.
     */
    public static String null2Empty(String str) {
        if (str == null) {
            return "";
        }

        return str;
    }

    /**
     * 验证给定字符串是否为真.
     * <p>描述:true、1或on均表示为真</p>
     * @param value 要验证的字符串
     * @return 当<code>value</code>的值为true、1或on返回真，否则返回假
     */
    public static boolean isBooleanTrue(String value) {
        if (value != null && !"".equals(value.trim())) {
            return "true".equalsIgnoreCase(value.trim()) || "1".equals(value.trim()) || "on".equalsIgnoreCase(value.trim());
        }

        return false;
    }

    /**
     * 生成指定长度的字符串<br/>
     * 例如生成10个0，batchCreateString("0", 5)<br/>，结果：00000
     * 5个ab连接的字符串, batchCreateString("ab", 5)，结果：ababababab
     * @param string	字符串
     * @param times		生成字符串的次数
     */
    public static String batchCreateString(String string, int times) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(string)) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(string);
        }
        return sb.toString();
    }

    /**
     * 使用<code>params</code>中的参数替换<code>source</code>中的参数符号.
     * @param source 待替换的字符串
     * @param params 参数集合
     * @return 替换后字符串
     */
    public static String replaceConstant(String source, Map<String, String> params) {
        if (org.apache.commons.lang3.StringUtils.isBlank(source)) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                source = org.apache.commons.lang3.StringUtils.replace(source, "{"+key+"}", params.get(key));
            }
        }

        return source;
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
                    source = pre + batchCreateString("*", length) + source.substring(startPos+length);
                } else {
                    source = pre + batchCreateString("*", source.length()- startPos);
                }
            }
        }

        return source;
    }

    /**
     * 去除url地址中的http://头并在前后加空格.
     */
    public static String formatUrlForSms(String url) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
            return url.replace("http://", " ") + " ";
        }

        return url;
    }

    private StringUtils(){}
}
