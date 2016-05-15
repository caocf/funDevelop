package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.*;

/**
 * 关联表注解.
 * <p>描述:定义要关联的表</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:36
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinTable {
    /** 表名. */
    String name();

    /** 别名. */
    String alias();

    /** 关联字段. */
    JoinColumn[] columns() default {};

    /** 关联类型（默认为右连接）. */
    String joinType() default "";
}