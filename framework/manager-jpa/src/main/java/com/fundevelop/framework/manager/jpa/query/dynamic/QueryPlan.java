package com.fundevelop.framework.manager.jpa.query.dynamic;

import com.fundevelop.persistence.entity.hibernate.BaseEntity;

import java.lang.annotation.*;

/**
 * 查询计划注解.
 * <p>描述:定义查询计划要返回的数据封装Bean</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/7 09:24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryPlan {
    /** 计划名称. */
    String name();

    /** 返回数据封装Bean. */
    Class<? extends BaseEntity<?>> returnType();
}