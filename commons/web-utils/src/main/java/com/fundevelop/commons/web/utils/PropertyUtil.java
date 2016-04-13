package com.fundevelop.commons.web.utils;

import com.fundevelop.commons.utils.LinkedProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * 系统属性工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/15 11:44
 */
public class PropertyUtil {
    /**
     * 载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的载入.
     * 文件路径使用Spring Resource格式, 文件编码使用UTF-8.
     */
    public static Properties loadProperties(List<Object> resourcesPaths) throws IOException {
        Properties props = new Properties();

        for (Object location : resourcesPaths) {
            if (location instanceof  String) {
                // 剔除classpath路径协议
                location = location.toString().replace("classpath*:/", "");
            }

            logger.debug("Loading properties file from:" + location);

            InputStream is = null;
            try {
                if (location instanceof  URL) {
                    is = ((URL)location).openStream();
                } else {
                    Resource resource = resourceLoader.getResource(location.toString());
                    is = resource.getInputStream();
                }
                propertiesPersister.load(props, new InputStreamReader(is, DEFAULT_ENCODING));
            } catch (IOException ex) {
                logger.warn("Could not load properties from classpath:" + location + ": " + ex.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return props;
    }

    /**
     * 获取所有的key.
     */
    public static Set<String> getKeys() {
        if (!initialized) {
            init();
        }

        return properties.stringPropertyNames();
    }

    /**
     * 获取键值对Map.
     */
    public static Map<String, String> getKeyValueMap() {
        Set<String> keys = getKeys();
        Map<String, String> values = new HashMap<String, String>(keys.size());
        for (String key : keys) {
            values.put(key, get(key));
        }
        return values;
    }

    /**
     * 获取属性值.
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        if (!initialized) {
            init();
        }
        String propertyValue = properties.getProperty(key);
        if (logger.isDebugEnabled()) {
            logger.debug("获取属性：{}，值：{}", key, propertyValue);
        }
        return propertyValue;
    }

    /**
     * 获取属性值.
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(String key, String defaultValue) {
        if (!initialized) {
            init();
        }
        String propertyValue = properties.getProperty(key);
        String value = StringUtils.defaultString(propertyValue, defaultValue);
        if (logger.isDebugEnabled()) {
            logger.debug("获取属性：{}，值：{}", key, value);
        }
        return value;
    }

    /**
     * 判断key对应的value是否和期待的一致.
     * @param key 键
     * @param expectValue 期望值
     * @return true：一致；false：不一致
     */
    public static boolean equalsWith(String key, String expectValue) {
        String value = get(key);
        return StringUtils.equals(value, expectValue);
    }

    /**
     * 向内存添加属性.
     *
     * @param key   键
     * @param value 值
     */
    public static void add(String key, String value) {
        properties.put(key, value);
        logger.debug("通过方法添加属性到内存：{}，值：{}", key, value);
    }

    public static Properties getActivePropertyFiles() {
        return activePropertyFiles;
    }

    public static String getProfile() {
        return PROFILE_ID;
    }

    /**
     * 初始化读取配置文件，读取的文件列表位于classpath下面的application-[type]-files.properties<br/>
     * <p/>
     * 多个配置文件会用最后面的覆盖相同属性值
     *
     * @param profile 配置文件类型，application-[profile]-files.properties
     * @throws IOException 读取属性文件时
     */
    public static void init(String profile) throws IOException {
        if (StringUtils.isBlank(profile)) {
            init();
        } else {
            if (!initialized) {
                synchronized (PropertyUtil.class) {
                    if (!initialized) {
                        PROFILE_ID = profile;
                        String fileNames = "application-" + profile + "-files.properties";
                        innerInit(fileNames);
                        initialized = true;
                    }
                }
            }
        }
    }

    /**
     * 初始化读取配置文件，读取的文件列表位于classpath下面的application-files.properties<br/>
     * <p/>
     * 多个配置文件会用最后面的覆盖相同属性值
     *
     * @throws IOException 读取属性文件时
     */
    private static void init() {
        if (!initialized) {
            synchronized (PropertyUtil.class) {
                if (!initialized) {
                    String fileNames = "application-files.properties";
                    PROFILE_ID = StringUtils.EMPTY;
                    try {
                        innerInit(fileNames);
                    } catch (Exception ex) {
                        logger.error("读取资源文件出错", ex);
                        throw new RuntimeException("读取资源文件出错", ex);
                    }

                    initialized = true;
                }
            }
        }
    }

    /**
     * 内部处理.
     *
     * @param fileName 资源列表文件地址
     * @throws IOException
     */
    private static void innerInit(String fileName) throws IOException {
        List<Object> propFiles = activePropertyFiles(fileName);
        logger.debug("读取属性文件：{}", ArrayUtils.toString(propFiles));
        properties = loadProperties(propFiles);

        if (logger.isDebugEnabled()) {
            Set<Object> keySet = properties.keySet();
            for (Object key : keySet) {
                logger.debug("property: {}, value: {}", key, properties.getProperty(key.toString()));
            }
        }
    }

    /**
     * 获取读取的资源文件列表.
     *
     * @param fileName 资源列表文件地址
     * @throws IOException
     */
    private static List<Object> activePropertyFiles(String fileName) throws IOException {
        logger.info("读取资源列表文件：" + fileName);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(fileName);
        // 默认的Properties实现使用HashMap算法，为了保持原有顺序使用有序Map
        activePropertyFiles = new LinkedProperties();

        if (resourceAsStream != null) {
            activePropertyFiles.load(resourceAsStream);
        }

        // 需要优先将jar中的资源文件进行加载
        Enumeration<URL> innerPropEnum = loader.getResources(INNER_PROPERTIES_NAME);
        List<Object> propFiles = new ArrayList<>();

        if (innerPropEnum != null && innerPropEnum.hasMoreElements()) {
            for (;innerPropEnum.hasMoreElements();) {
                URL propUrl = innerPropEnum.nextElement();
                propFiles.add(propUrl);
            }
        }

        for (Object proFileKey : activePropertyFiles.keySet()) {
            propFiles.add(activePropertyFiles.getProperty(proFileKey.toString()));
        }

        return propFiles;
    }

    private PropertyUtil(){}

    private static  final String INNER_PROPERTIES_NAME = "fun-inner-application.properties";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Properties properties;
    private static PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();
    private static Properties activePropertyFiles = null;
    private static String PROFILE_ID = StringUtils.EMPTY;
    public static boolean initialized = false; // 是否已初始化
}
