package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.lang.annotation.*;

/**
 * 忽略属性注解.
 * <p>描述:用户忽略不需要进行查询的属性</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:41
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreProperties {
    /** 属性列表. */
    IgnoreProperty[] value();
}