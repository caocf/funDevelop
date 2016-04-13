package com.fundevelop.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/10 9:43
 */
public class ValidationUtils {
    /**
     * 验证身份证合法性.
     */
    public static boolean validIdNo(String idNo) {
        // 准备正则表达式（身份证有15位和18位两种，身份证的最后一位可能是字母）
        String regex = "(\\d{14}\\w)|\\d{17}\\w";
        // 准备开始匹配，判断所有的输入是否是正确的
        Pattern regular = Pattern.compile(regex); // 创建匹配的规则Patter
        Matcher matcher = regular.matcher(idNo);// 创建一个Matcher
        return matcher.matches();
    }

    /**
     * 验证邮箱地址的合法性.
     */
    public static boolean validEmail(String email) {
        Pattern pattern = Pattern.compile("^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 验证手机号码的合法性.
     * 参见：http://blog.csdn.net/fengshi_sh/article/details/12085307
     */
    public static boolean validPhone(String phone) {
        Pattern p = Pattern.compile("^0?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 从身份证号中提取出生日期.
     */
    public static String getBirthDayFromIdNo(String idNo) {
        // 准备验证规则
        Pattern BirthDayRegular = Pattern.compile("(\\d{6})(\\d{8})(.*)");
        // .*连在一起就意味着任意数量的不包含换行的字符
        Pattern YearMonthDayRegular = Pattern
                .compile("(\\d{4})(\\d{2})(\\d{2})");
        Matcher matcher = BirthDayRegular.matcher(idNo);

        if (matcher.matches()) {
            Matcher matcher2 = YearMonthDayRegular
                    .matcher(matcher.group(2));
            if (matcher2.matches()) {
                return matcher2.group(1) + "-" + matcher2.group(2) + matcher2.group(3);
            }
        }

        return null;
    }

    /**
     * 验证组织机构代码合法性.
     */
    public static final boolean validOrganizationCode(String organizationCode) {
        organizationCode = organizationCode.toLowerCase();
        int[] ws = { 3, 7, 9, 10, 5, 8, 4, 2 };
        String str = "0123456789abcdefghijklmnopqrstuvwxyz";
        String regex = "^([0-9a-z]){8}-[0-9|x]$";
        if (!organizationCode.matches(regex)) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += str.indexOf(String.valueOf(organizationCode.charAt(i))) * ws[i];
        }

        int c9 = 11 - (sum % 11);
        String sc9 = c9 == 10 ? "x" : String.valueOf(c9);

        return sc9.equals(String.valueOf(organizationCode.charAt(9)));
    }

    /**
     * 构造函数.
     */
    private ValidationUtils(){}
}
