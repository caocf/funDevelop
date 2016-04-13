package com.fundevelop.commons.utils;

import java.util.concurrent.*;

/**
 * 后台任务辅助类.
 * <p>描述:主要负责在后台执行任务以免堵塞主任务执行</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 19:21
 */
public class BackgroundJobHelps {
    /** 线程池. */
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * 添加任务.
     * @param job 要后台执行的任务
     */
    public static void addJob(Runnable job) {
        threadPool.execute(job);
    }

    /**
     * 运行后台任务.
     */
    public static <T> Future<T> runJob(Callable<T> job) {
        return threadPool.submit(job);
    }

    /**
     * 运行带超时功能的任务.
     */
    public static <T> T runJob(Callable<T> job, long timeout) {
        Future<T> future = threadPool.submit(job);

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            future.cancel(true);
            throw new RuntimeException("运行超时功能任务失败", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("运行超时功能任务失败", e);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("运行超时功能任务失败", e);
        }
    }
}
