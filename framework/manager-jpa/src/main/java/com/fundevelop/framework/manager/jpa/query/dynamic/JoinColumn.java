package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.*;

/**
 * 关联字段注解.
 * <p>描述:用于说明关联表中的关联字段</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinColumn {
    /** 字段名. */
    String name();

    /** 父表关联字段（默认为主表ID）. */
    String referencedColumnName() default "";
}