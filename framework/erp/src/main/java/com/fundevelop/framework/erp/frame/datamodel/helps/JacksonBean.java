package com.fundevelop.framework.erp.frame.datamodel.helps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有要使用Jackson自动将实体转换为Json的默认接口实现类.
 * <p>描述:所有要自动转换的实体均可以继承该类</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 15:33
 */
public class JacksonBean implements BeanFilter {
    @Override
    public void addFilter(Class clazz, String filterId, String... propertys) {
        filterProvider.addFilter(clazz,filterId,propertys);
    }

    @Override
    @Transient
    @JsonIgnore
    public JacksonFilterProvider getFilter() {
        return filterProvider;
    }

    @Override
    public void addType(String name, Class<?> type) {
        types.put(name, SimpleType.construct(type));
    }

    @Override
    @JsonIgnore
    public Map<String, JavaType> getTypes() {
        return types;
    }

    /** Jackson过滤器. */
    private JacksonFilterProvider filterProvider = new JacksonFilterProvider();
    /** 自定义类型. */
    private Map<String, JavaType> types = new HashMap<>();
}
