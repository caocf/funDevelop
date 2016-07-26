package com.fundevelop.framework.erp.audit;

import java.lang.annotation.*;

/**
 * 日志记录管理注解.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/6 18:53
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ErpLogger {
    /** 模块名称 */
    String model() default "";

    /** 操作 */
    String action() default "";

    /** 操作内容 */
    String logger() default "";

    /** 操作成功 */
    String opSucess() default "成功";

    /** 操作失败 */
    String opError() default "失败";

    /** 是否记录日志，默认为记录日志  */
    boolean log() default true;

    /** 整个类均不进行日志记录，默认记录  */
    boolean globaldisable() default false;
}
