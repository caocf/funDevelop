package com.fundevelop.job.quartz;

import com.fundevelop.commons.utils.DateUtils;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.job.quartz.annoations.Job;
import com.fundevelop.job.quartz.annoations.JobMethod;
import com.fundevelop.job.quartz.entity.ScheduleJob;
import com.fundevelop.job.quartz.manager.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描通过注解方式添加的计划任务.
 * <p>描述:自动扫描使用@Job注解标记的计划任务</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 19:59
 */
@Component
public class JobAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ServletContextListener {
    /** 日志记录对象. */
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Class<?>> jobBeans = new HashMap<String, Class<?>>();
    private boolean isFinish = false;

    /**
     * 构造函数.
     */
    public JobAnnotationBeanPostProcessor() {
        super();
        SpringContextHolder.addListener(this);
    }

    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        if (isFinish) return null;

        // 检查类上是否有@Job注解
        Job job = beanClass.getAnnotation(Job.class);

        if (job != null && !jobBeans.containsKey(beanName)) {
            jobBeans.put(beanName, beanClass);
        }

        return null;
    }

    /**
     * 注册登记注解标记的计划任务.
     */
    private void registerJob() {
        if (jobBeans.size() > 0) {
            for (String beanName : jobBeans.keySet()) {
                registerJob(beanName, jobBeans.get(beanName));
            }
        }

        jobBeans = null;
    }

    /**
     * 注册登记注解标记的计划任务.
     */
    private void registerJob(String beanName, Class<?> beanClass) {
        Job job = beanClass.getAnnotation(Job.class);
        JobService service = (JobService)SpringContextHolder.getBeanNotRequired("jobService");

        if (!service.jobExists(job.name())) {
            final ScheduleJob newJob = new ScheduleJob();
            newJob.setName(job.name());
            newJob.setCronExpression(job.cronExpression());
            newJob.setConcurrent(job.concurrent());
            newJob.setDescription(job.desc());
            newJob.setTargetBean(beanName);
            newJob.setEnabled(true);

            try {
                newJob.setStartTime(DateUtils.toDate(job.startTime()));
            } catch (Exception e) {
                logger.error("设置计划任务开始时间失败", e);
            }

            try {
                newJob.setEndTime(DateUtils.toDate(job.endTime()));
            } catch (Exception e) {
                logger.error("设置计划任务结束时间失败", e);
            }

            newJob.setPriority(job.priority());
            newJob.setCalendarName(job.calendarName());

            ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    JobMethod jobMethod = method.getAnnotation(JobMethod.class);

                    if (jobMethod != null) {
                        newJob.setTargetMethod(method.getName());
                        return;
                    }
                }
            });

            try {
                service.save(newJob);
            } catch (Exception ex) {
                logger.error("通过注解方式添加计划任务["+beanClass.getName()+"]失败", ex);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("计划任务类[" + beanClass.getName() + "]在数据库中已经存在，不再进行处理");
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        registerJob();
        isFinish = true;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
