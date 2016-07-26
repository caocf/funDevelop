package com.fundevelop.framework.base.converter;

import com.fundevelop.commons.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 日期格式转换器.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/25 13:43
 */
public class DateConverter implements Converter<String, Date> {
    /** DATETIME_PATTERN(String):yyyy-MM-dd HH:mm:ss. */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** DATE_PATTERN(String):yyyy-MM-dd. */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /** MONTH_PATTERN(String):yyyy-MM. */
    public static final String MONTH_PATTERN = "yyyy-MM";

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     * @author <a href="mailto:yangmujiang@xiaomashijia.com">Reamy(杨木江)</a>
     * @date 2014-09-02 13:41:39
     */
    @Override
    public Date convert(String source) {
        if (StringUtils.hasText(source)) {
            try {
                return DateUtils.toDate(source);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        }

        return null;
    }
}
