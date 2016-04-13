package com.fundevelop.framework.openapi.annoations;

import java.lang.annotation.*;

/**
 * OpenAPI发布服务注解.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/15 8:24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestCmdMapping {
    /**
     * Service的CMD
     */
    String cmd() default "";

    /**
     * 资源名称
     */
    String name() default "";

    /**
     * 资源描述
     */
    String description() default "";

    /**
     * 是否缓存结果
     */
    boolean cache() default false;

    /**
     * 接收请求的对象类型
     */
    //Class<? extends BaseRestRequest> requestType() default DynamicParameterRestRequest.class;

    /**
     * 权限拦截配置
     */
    //XmsjRestAuthority authority() default XmsjRestAuthority.NONE;

    /**
     * 重试次数
     */
    int retryTimes() default 0;

    /**
     * 请求超时时间
     */
    int timeout() default 0;
}
