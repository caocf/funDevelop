package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.*;

/**
 * 字段类型注解.
 * <p>描述:标识查询字段对应的数据类型</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:44
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldType {
    /** 查询字段属性名称. */
    String name();

    /** 字段对应的数据类型. */
    Class<?> type();
}