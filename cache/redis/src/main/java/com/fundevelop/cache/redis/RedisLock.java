package com.fundevelop.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Redis锁管理类.
 * <p>描述:对Redis资源进行锁定处理，以防出现资源竞争</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 15:03
 */
public class RedisLock {
    /** 加锁标志. */
    private static final String LOCKED = "TRUE";
    /** 毫秒与毫微秒的换算单位 1毫秒=1000000毫微秒. */
    private static final long MILLI_NANO_CONVERSION = 1000*1000L;
    /** 默认超时时间（毫秒）. */
    private static final long DEFAULT_TIME_OUT = 3*60*1000;
    private static Random RANDOM = new Random();
    /** 锁的超时时间（秒），过期删除. */
    private static final int EXPIRE = 3*60;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String key;
    /** 锁状态标志. */
    private boolean locked = false;

    /**
     * 获取Redis锁对象.
     */
    public static RedisLock getRedisLock(String key) {
        return new RedisLock(key);
    }

    /**
     * 加锁.
     * <p>使用方式：<br/>
     * lock();<br/>
     *
     * try {<br/>
     *     // 业务代码<br/>
     * } finally {<br/>
     *     unlock();<br/>
     * }
     * </p>
     * @return true：成功；false：失败
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT);
    }

    /**
     * 解锁.
     * <p>无论加锁是否成功，都需要调用该方法进行解锁</p>
     */
    public void unlock() {
        if (locked) {
            RedisUtil.del(key);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("RedisLock解锁：{}", key);
        }
    }

    /**
     * 加锁.
     * <p>使用方式：<br/>
     * lock();<br/>
     *
     * try {<br/>
     *     // 业务代码<br/>
     * } finally {<br/>
     *     unlock();<br/>
     * }
     * </p>
     * @param timeout 超时时间（毫秒）
     * @return true：成功；false：失败
     */
    public boolean lock(long timeout) {
        return lock(timeout, EXPIRE);
    }

    /**
     * 加锁.
     * <p>使用方式：<br/>
     * lock();<br/>
     *
     * try {<br/>
     *     // 业务代码<br/>
     * } finally {<br/>
     *     unlock();<br/>
     * }
     * </p>
     * @param timeout 超时时间（毫秒）
     * @param expire 锁的超时时间（秒），过期删除
     * @return true：成功；false：失败
     */
    public boolean lock(long timeout, int expire) {
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;

        try {
            if(timeout>0){
                while ((System.nanoTime()-nano)<timeout) {
                    if (RedisUtil.setnx(key, LOCKED) == 1) {
                        RedisUtil.expire(key, expire);
                        locked = true;

                        if (logger.isTraceEnabled()) {
                            logger.trace("RedisLock锁定：{}", key);
                        }
                        return locked;
                    }

                    // 短暂休眠，避免出现死锁
                    Thread.sleep(3, RANDOM.nextInt(500));
                    if (logger.isTraceEnabled()) {
                        logger.trace("RedisLock等待资源：{}", key);
                    }
                }
            }else{
                if (RedisUtil.setnx(key, LOCKED) == 1) {
                    if(expire>0){
                        RedisUtil.expire(key, expire);
                    }
                    locked = true;
                    return locked;
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Locking error", ex);
        }

        return false;

    }

    /**
     * 构造函数.
     * @param key key
     */
    private RedisLock(String key) {
        this.key = key;
    }
}
