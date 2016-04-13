package com.fundevelop.commons.utils;

/**
 * 后台任务执行接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 19:19
 */
public abstract class BackgroundJob implements Runnable {
    /** 任务参数. */
    protected Object[] params;

    /**
     * 构造函数.
     * @param params 任务参数
     */
    public BackgroundJob(Object... params) {
        this.params = params;
    }
}
