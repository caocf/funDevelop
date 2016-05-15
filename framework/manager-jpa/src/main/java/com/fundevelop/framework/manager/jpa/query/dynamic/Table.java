package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.*;

/**
 * 查询主表注解.
 * <p>描述:定义查询计划中使用的主表</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /** 表名. */
    String name();

    /** 别名. */
    String alias() default "";

    /** 主键字段名. */
    String pkColumn() default "id";
}