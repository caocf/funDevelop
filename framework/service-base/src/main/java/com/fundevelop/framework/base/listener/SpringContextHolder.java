package com.fundevelop.framework.base.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.annotation.Annotation;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Spring Context持有类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/18 9:47
 */
@WebListener
public class SpringContextHolder implements ServletContextListener {
    /**
     * 获取Spring上下文.
     */
    public static ApplicationContext getCtx() {
        return ctx;
    }

    /**
     * 根据注解获取类.
     * @see org.springframework.beans.factory.ListableBeanFactory#getBeansWithAnnotation(Class)
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        if (ctx == null) {
            throw new IllegalStateException("applicaitonContext未注入，等待应用启动完成后再进行调用");
        }
        return ctx.getBeansWithAnnotation(annotationType);
    }

    /**
     * 从Spring中获取Bean.
     */
    public static <T> T getBean(Class<T> requiredType) {
        if (ctx == null) {
            throw new IllegalStateException("applicaitonContext未注入，等待应用启动完成后再进行调用");
        }

        return ctx.getBean(requiredType);
    }

    /**
     * 从Spring中获取Bean.
     */
    public static Object getBean(String beanName) {
        if (ctx == null) {
            throw new IllegalStateException("applicaitonContext未注入，等待应用启动完成后再进行调用");
        }
        return ctx.getBean(beanName);
    }

    /**
     * 从Spring中获取Bean.
     * @return 如果存在则返回对应的Bean；如果不存在则返回null
     */
    public static Object getBeanNotRequired(String beanName) {
        if (ctx != null && ctx.containsBean(beanName)) {
            return getBean(beanName);
        }

        return null;
    }

    /**
     * 添加监听器.
     * @param listener 要添加的监听器
     */
    public static void addListener(ServletContextListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 触发监听器.
     */
    public void fireEvent(ServletContextEvent event) {
        for (ServletContextListener listener : listeners) {
            listener.contextInitialized(event);
        }
    }

    /**
     * 获取系统根目录.
     */
    public static String getRootPath() {
        return rootPath;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        rootPath = sce.getServletContext().getRealPath("/");
        ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
        fireEvent(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (ServletContextListener listener : listeners) {
            listener.contextDestroyed(sce);
        }
        ctx = null;
        unbindResource();

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.warn("stopping... ", driver);
            } catch (SQLException e) {
                logger.error("stopping driver ",driver);
            }
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for(Thread t:threadArray) {
            if(t.getName().contains("Abandoned connection cleanup thread")||t.getName().contains("task_excutor")) {
                synchronized(t) {
                    t.stop();
                }
            }
        }
    }

    /**
     * 释放Spring Data事务资源.
     */
    private void unbindResource() {
        Map<Object, Object> resource = TransactionSynchronizationManager.getResourceMap();

        if (resource != null) {
            for (Object key : resource.keySet()) {
                TransactionSynchronizationManager.unbindResourceIfPossible(key);
            }
        }
    }

    /** Spring框架上下文. */
    private static ApplicationContext ctx = null;
    /** 系统根目录. */
    private static String rootPath;
    private static final List<ServletContextListener> listeners = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(getClass());
}
