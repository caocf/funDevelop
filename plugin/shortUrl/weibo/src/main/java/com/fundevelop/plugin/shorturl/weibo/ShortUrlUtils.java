package com.fundevelop.plugin.shorturl.weibo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundevelop.cache.redis.RedisLock;
import com.fundevelop.cache.redis.RedisUtil;
import com.fundevelop.commons.utils.StringUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 短链接工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/8/12 22:22
 */
public class ShortUrlUtils {
    /**
     * 根据原始URL地址获取短链接.
     * @param sourceUrl 原始URL地址
     * @return 短链接
     */
    public static String getShortUrl(String sourceUrl) {
        if (org.apache.commons.lang3.StringUtils.isBlank(sourceUrl)) {
            return "";
        }

        boolean useRedisCache = StringUtils.isBooleanTrue(PropertyUtil.get("shorturl.useRedisCache", "true"));

        if (useRedisCache) {
            RedisLock lock = RedisLock.getRedisLock(CACHE_KEY+"lock:"+sourceUrl);

            try {
                lock.lock();

                String cacheKey = CACHE_KEY+sourceUrl;

                if (RedisUtil.exists(cacheKey)) {
                    return RedisUtil.get(cacheKey);
                }

                String shortUrl = getShortUrlFromServer(sourceUrl);

                if (org.apache.commons.lang3.StringUtils.isNotBlank(shortUrl)) {
                    RedisUtil.set(cacheKey, shortUrl, CACHE_EXPIRETIME);
                }

                return shortUrl;
            } finally {
                lock.unlock();
            }
        } else {
            return getShortUrlFromServer(sourceUrl);
        }
    }

    /**
     * 从新浪微博服务器换取短链接.
     */
    private static String getShortUrlFromServer(String sourceUrl) {
        try {
            String url = PropertyUtil.get("shorturl.weibo.host");

            if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
                throw new RuntimeException("请在application.properties配置文件中配置shorturl.weibo.host属性(新浪微博获取短链接服务器请求地址)");
            }

            try {
                sourceUrl = URLEncoder.encode(sourceUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {}

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpgets = new HttpGet(url+sourceUrl);
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity httpEntity = response.getEntity();
            Object json = null;

            if (httpEntity != null) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                }

                InputStream instreams = httpEntity.getContent();
                json = mapper.readValue(httpEntity.getContent(), Map.class);
                instreams.close();
                httpgets.abort();
            }

            logger.debug("获取短链接请求结果：{}",json);

            if (json != null) {
                List<Map> urls = (List<Map>) ((Map)json).get("urls");

                if (urls != null && urls.size() > 0) {
                    Map result = urls.get(0);

                    if ((Boolean)result.get("result")) {
                        String retShortUrl = (String)result.get("url_short");

                        return retShortUrl;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("使用新浪微博获取短链接失败：原始URL地址：{}", sourceUrl, ex);
        }

        return "";
    }

    /** 持有Jackson单例, 避免重复创建ObjectMapper消耗资源. */
    private static ObjectMapper mapper;

    /** 短链接缓存有效期（1天）. */
    private static final int CACHE_EXPIRETIME = 24*60*60;
    /** 短链接缓存前缀. */
    private static final String CACHE_KEY = "fun:shortUrl:";

    private static Logger logger = LoggerFactory.getLogger(ShortUrlUtils.class);

    private ShortUrlUtils() {}
}
