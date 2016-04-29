package com.fundevelop.job.quartz.annoations;

import java.lang.annotation.*;

/**
 * 计划任务运行方法注解.
 * <p>描述:标注那个方法是计划任务运行方法</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:23
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobMethod {
}
