package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 字段注解.
 * <p>描述:用于将字段与属性进行绑定</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:40
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface Column {
    /** 绑定字段名. */
    String name() default "";

    /** 绑定表别名. */
    String table() default "";
}