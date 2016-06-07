package com.fundevelop.framework.erp.frame.datamodel.helps;

import com.fasterxml.jackson.databind.JavaType;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用Jackson主动将实体转换为Json时必须实现的接口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 15:34
 */
public interface BeanFilter extends Serializable {
    /** 过滤属性前缀. */
    public final static String filterPre = "F_";
    /** 过滤器ID与实体类名分割符. */
    public final static String FILTERSPLIT = "-C-";

    /**
     * 添加Jackson过滤器.
     * @param clazz 实体类
     * @param filterId 过滤器ID，以实体中JsonFilter注解中的名称对应，通过in（包含）、out（排除）关键字来指定过滤方式。如：F_filter1_in，F_filter1_out（F_filter1）
     * @param propertys 要过滤的属性
     */
    @SuppressWarnings("rawtypes")
    public void addFilter(Class clazz, String filterId, String... propertys);

    /**
     * 获取Jackson过滤器.
     * @return Jackson过滤器
     */
    public JacksonFilterProvider getFilter();

    /**
     * 添加自定义类型.
     * @param name 类型名称
     * @param type 对应的数据类型
     */
    public void addType(String name, Class<?> type);

    /**
     * 获取自定义类型.
     */
    public Map<String, JavaType> getTypes();
}
