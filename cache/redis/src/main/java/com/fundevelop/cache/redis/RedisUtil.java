package com.fundevelop.cache.redis;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis缓存工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 14:31
 */
public class RedisUtil {
    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private static ThreadLocal<ShardedJedis> jedisThreadLocalHolder = new ThreadLocal<ShardedJedis>();
    /** jedis缓存. */
    private static Map<ShardedJedis, Thread> jedisCache = new ConcurrentHashMap<ShardedJedis, Thread>();
    /** jedis超时时间（15秒）. */
    private static final int timeout = 15000;
    private static Boolean timerTaskIsRunning = false;
    private static final Timer timer = new Timer();
    /** 连接超时未还池监测任务. */
    private static TimerTask timeoutCheckTask = new TimerTask() {
        @Override
        public void run() {
            checkDieThread();
        }
    };

    /** Redis连接池. */
    private static ShardedJedisPool shardedJedisPool;

    public static Long setnx(String key, String value) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.setnx(key, value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:setnx error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 获取集合的元素个数.
     */
    public static Long scard(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.scard(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:scard error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 向集合中添加元素.
     */
    public static Long sadd(String key, String... members) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.sadd(key, members);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:sadd error, key: {},members: {}", key, members, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 获取集合中元素.
     */
    public static String spop(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.spop(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:spop error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 从集合中移除元素.
     */
    public static Long srem(String key, String... members) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.srem(key, members);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:srem error, key: {},members: {}", key, members, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 获取集合中的所有元素.
     */
    public static Set<String> smembers(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.smembers(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:smembers error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 判断集合中是否存在指定的元素.
     */
    public static boolean sismember(String key, String member) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.sismember(key, member);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:sismember error, key: {},member: {}", key, member, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * hset.
     */
    public static Long hset(String key,String field,String value){
        try {
            ShardedJedis jedis = getJedis();
            return jedis.hset(key,field,value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hset error, key: {},field: {},value: {}", key, field, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    public static String hget(String key, String field) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.hget(key, field);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hget error, key: {},field: {}", key, field, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    public static Long hdel(String key,String field){
        ShardedJedis jedis = getJedis();

        try {
            return jedis.hdel(key,field);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hdel error, key: {},field: {}", key, field, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    public static boolean hexists(String key, String field) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hexists error, key: {},field: {}", key, field, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * hincr.
     */
    public static Long hincr(String key,String field){
        try {
            ShardedJedis jedis = getJedis();
            return jedis.hincrBy(key,field,1L);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hincr error, key: {},field: {}", key, field, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * hincrBy.
     */
    public static Long hincrBy(String key,String field,Long value){
        try {
            ShardedJedis jedis = getJedis();
            return jedis.hincrBy(key,field,value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:hincrBy error, key: {},field: {}, value:{}", key, field, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 自增缓存键中的值.
     */
    public static Long incr(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.incr(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:incr error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 自减缓存中的键值.
     */
    public static Long decr(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.decr(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:decr error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 设置缓存.
     * @param key 键
     * @param value 值
     * @return Status code reply
     */
    public static String set(String key, String value) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.set(key, value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:set error, key: {}, value: {}", key, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 设置缓存.
     * @param key 键
     * @param value 值
     * @param expire 失效时间(秒)
     * @return Status code reply
     */
    public static String set(String key, String value, int expire) {
        String status = null;

        try {
            ShardedJedis jedis = getJedis();
            status = jedis.set(key, value);
            jedis.expire(key, expire);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:set error, key: {}, value: {}, expire: {}", key, value, expire, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }

        return status;
    }

    /**
     * 在名称为key的list尾添加一个值为value的元素.
     * @return 添加的数量
     */
    public static Long rpush(String key, String... value) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.rpush(key, value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:rpush error, key: {}, value: {}", key, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 在名称为key的list头添加一个值为value的 元素.
     * @return 添加的数量
     */
    public static Long lpush(String key, String... value) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.lpush(key, value);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:lpush error, key: {}, value: {}", key, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 返回名称为key的list的长度.
     */
    public static Long llen(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.llen(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:llen error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 返回并删除名称为key的list中的首元素.
     */
    public static String lpop(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.lpop(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:lpop error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 返回并删除名称为key的list中的尾元素.
     */
    public static String rpop(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.rpop(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:rpop error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 设置过期时间.
     */
    public static Long expire(String key, int seconds) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.expire(key, seconds);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:expire error, key: {}, seconds: {}", key, seconds, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 获取缓存中的值.
     * @param key 键
     * @return 缓存中的值
     */
    public static String get(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.get(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:get error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 删除缓存中的键.
     */
    public static Long del(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.del(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:del error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 验证给定的key是否在缓存中存在.
     */
    public static boolean exists(String key) {
        try {
            ShardedJedis jedis = getJedis();
            return jedis.exists(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:exists error, key: {}", key, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 验证指定的缓存Key在Redis中是否存在，如果不存在则进行值的初始化，否则不进行设置.
     */
    public static String noExistToSet(String key, String value) {
        try {
            ShardedJedis jedis = getJedis();
            if (!jedis.exists(key)) {
                jedis.set(key, value);
            }

            return jedis.get(key);
        } catch (Exception ex) {
            returnBrokenResource();
            logger.error("redis:noExistToSet error, key: {}, value: {}", key, value, ex);
            throw new RuntimeException("Redis操作失败！", ex);
        }
    }

    /**
     * 释放坏死的连接.
     */
    @SuppressWarnings("deprecation")
    private static void returnBrokenResource() {
        ShardedJedis jedis = null;

        synchronized (Thread.currentThread()) {
            jedis = jedisThreadLocalHolder.get();

            if (jedis != null) {
                jedisThreadLocalHolder.remove();
            }
        }

        if (jedis != null) {
            jedisCache.remove(jedis);

            try {
                shardedJedisPool.returnBrokenResource(jedis);
            } catch (Exception ex) {
                logger.warn("redis连接还池失败！", ex);
            }
        }
    }

    /**
     * 释放本地线程池资源.
     */
    @SuppressWarnings("deprecation")
    public static void realRealese() {
        ShardedJedis jedis = null;

        synchronized (Thread.currentThread()) {
            jedis = jedisThreadLocalHolder.get();

            if (jedis != null) {
                jedisThreadLocalHolder.remove();
            }
        }

        if (jedis != null) {
            jedisCache.remove(jedis);

            try {
                shardedJedisPool.returnResource(jedis);
            } catch (Exception ex) {
                logger.warn("redis连接还池失败！", ex);
            }
        }
    }

    /**
     * 获取Jedis实例.
     */
    private static ShardedJedis getJedis() {
        if (shardedJedisPool == null) {
            shardedJedisPool = (ShardedJedisPool)SpringContextHolder.getBean("shardedJedisPoolCache");

            synchronized (timerTaskIsRunning) {
                if (!timerTaskIsRunning) {
                    timer.schedule(timeoutCheckTask, timeout, timeout);
                    timerTaskIsRunning = true;
                }
            }
        }

        ShardedJedis jedis = null;

        synchronized (Thread.currentThread()) {
            jedis = jedisThreadLocalHolder.get();
        }

        try  {
            if (jedis == null) {
                jedis = shardedJedisPool.getResource();
                jedisThreadLocalHolder.set(jedis);
                jedisCache.put(jedis, Thread.currentThread());
            }
        } catch (Exception e) {
            logger.error("get jedis fail: ", e);
        }

        return jedis;
    }

    /**
     * 检测已经不活动的进程.
     */
    @SuppressWarnings("deprecation")
    private static synchronized void checkDieThread() {
        List<ShardedJedis> timeoutJedis = new ArrayList<ShardedJedis>();

        for (ShardedJedis jedis :jedisCache.keySet()) {
            Thread cacheThread = jedisCache.get(jedis);

            if (!cacheThread.isAlive()) {
                timeoutJedis.add(jedis);
            }
        }

        for (ShardedJedis jedis : timeoutJedis) {
            jedisCache.remove(jedis);

            try {
                shardedJedisPool.returnResource(jedis);
            } catch (Exception ex) {
                logger.warn("redis连接还池失败！", ex);
            }
        }

        synchronized (Thread.currentThread()) {
            jedisThreadLocalHolder.remove();
        }
    }

    /**
     * 构造函数.
     */
    private RedisUtil() {}
}
