package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 忽略属性注解.
 * <p>描述:用户忽略不需要进行查询的属性</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:42
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface IgnoreProperty {
    /** 属性名称. */
    String name();
}