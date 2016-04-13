package com.fundevelop.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ognl.OgnlOps;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Bean工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/15 8:02
 */
public class BeanUtils {
    /**
     * 将实体对象转换为Json字符串.
     * @throws JsonProcessingException
     */
    public static String toJson(Object bean) throws JsonProcessingException {
        initJackson();

        if (bean == null) {
            return null;
        }

        return mapper.writeValueAsString(bean);
    }

    /**
     * 将Json字符串还原为实体对象.
     * @throws IOException
     */
    public static <T> T toBean(String json, Class<T> beanClass) throws IOException {
        initJackson();

        return mapper.readValue(json, beanClass);
    }

    /**
     * 将<code>value</code>根据在<code>valueType</code>中的数据类型进行转换.
     * @param valueType 数据类型
     * @param value 要转换的值
     * @return <code>value</code>类型转换后的对象
     * @throws Exception
     */
    public static Object convertValue(Class<?> valueType, Object value) throws Exception {
        if (valueType == Date.class) {
            return DateUtils.toDate(value);
        }

        return OgnlOps.convertValue(value, valueType);
    }

    /**
     * 将<code>value</code>根据在<code>clazz</code>中的<code>propertyName</code>的数据类型进行转换.
     * @param clazz <code>propertyName对应的实体类
     * @param propertyName 属性名
     * @param value 要转换的值
     * @return <code>value</code>类型转换后的对象
     */
    public static Object convertValue (Class clazz, String propertyName, Object value) {
        try {
            int pos = propertyName.indexOf(".");

            if (pos != -1) {
                String p = propertyName.substring(0,pos);
                String s = propertyName.substring(pos+1);

                return convertValue(org.springframework.beans.BeanUtils.findPropertyType(p, new Class[]{clazz}),s,value);
            }

            Class toType = org.springframework.beans.BeanUtils.findPropertyType(propertyName, new Class[]{clazz});

            return convertValue(toType, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据类型转换失败",e);
        }
    }

    /**
     * 初始化Jackson对象.
     */
    private static void initJackson() {
        if (mapper == null) {
            synchronized (BeanUtils.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                    mapper.setTimeZone(TimeZone.getDefault());
                }
            }
        }
    }

    /** 持有Jackson单例, 避免重复创建ObjectMapper消耗资源. */
    private static ObjectMapper mapper;
}
