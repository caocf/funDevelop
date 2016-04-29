package com.fundevelop.job.quartz;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.job.quartz.entity.ScheduleJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 计划任务运行工厂类.
 * <p>Company:上海达加马信息科技有限公司</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:34
 */
public class JobFactory  implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ScheduleJob job = (ScheduleJob)context.getMergedJobDataMap().get("ScheduleJob");

        if (logger.isDebugEnabled()) {
            logger.debug("准备执行计划任务：" + job.getName());
        }

        Object targetBean = SpringContextHolder.getBeanNotRequired(job.getTargetBean());

        if (targetBean != null) {
            try {
                Method jobMethod = null;

                try {
                    jobMethod = targetBean.getClass().getMethod(job.getTargetMethod(),ScheduleJob.class);

                    if (jobMethod != null) {
                        ReflectionUtils.makeAccessible(jobMethod);
                        jobMethod.invoke(targetBean, job);
                    }
                } catch (NoSuchMethodException ex) {
                    jobMethod = targetBean.getClass().getMethod(job.getTargetMethod());
                    ReflectionUtils.makeAccessible(jobMethod);
                    jobMethod.invoke(targetBean);
                } finally {
                    //TODO 如果计划任务中使用Redis，需要执行完任务后进行释放
//                    RedisUtil.realRealese();
                }
            } catch (Exception ex) {
                logger.error("执行计划任务["+job.getName()+"],无法调用目标Bean("+job.getTargetBean()+")中的执行方法："+job.getTargetMethod(),ex);
            }
        } else {
            logger.error("执行计划任务["+job.getName()+"]时找不到对应的目标Bean："+job.getTargetBean());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("计划任务[" + job.getName() + "]执行完成");
        }
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
