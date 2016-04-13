package com.fundevelop.commons.utils;

/**
 * 字符串工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 11:37
 */
public class StringUtils {
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

    private StringUtils(){}
}
