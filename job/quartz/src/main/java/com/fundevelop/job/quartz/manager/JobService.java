package com.fundevelop.job.quartz.manager;

import com.fundevelop.commons.utils.BackgroundJob;
import com.fundevelop.commons.utils.BackgroundJobHelps;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.job.quartz.JobFactory;
import com.fundevelop.job.quartz.StatefulJobFactory;
import com.fundevelop.job.quartz.dao.JobDao;
import com.fundevelop.job.quartz.entity.ScheduleJob;
import com.fundevelop.job.quartz.rpc.JobDubboService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * 计划任务服务类.
 * <p>描述:负责管理计划任务</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:44
 */
@Component
public class JobService extends AbstractManager<ScheduleJob, Long, JobDao> {
    /**
     * 定义计划任务允许的操作指令.
     */
    public static enum OP {
        /** 加入计划. */
        add,
        /** 暂停. */
        pause,
        /** 恢复. */
        resume,
        /** 立即运行一次. */
        runOnce,
        /** 停止计划. */
        cancel,

        /** 获取运行中任务. */
        getRunJobs
    }

    /**
     * 验证计划任务是否已经存在.
     */
    public boolean jobExists(String name) {
        ScheduleJob job = getDao().getByEnvAndName(getEnv(), name);

        return (job != null);
    }

    /**
     * 从Quartz中删除任务.
     * @param name 要删除的任务名称
     * @throws Exception
     */
    public void deleteJob(String name) throws Exception {
        deleteJob(getEnv(), name);
    }

    /**
     * 暂停任务.
     * @throws Exception
     */
    public void pauseJob(String name) throws Exception {
        pauseJob(getEnv(), name);
    }

    /**
     * 恢复任务.
     * @throws Exception
     */
    public void resumeJob(String name) throws Exception {
        resumeJob(getEnv(), name);
    }

    /**
     * 获取任务运行状态.
     * @throws Exception
     */
    public Trigger.TriggerState getJobState(String name) throws Exception {
        return getJobState(getEnv(), name);
    }

    /**
     * 获取任务运行状态.
     * @param env 所属环境
     * @param name 任务名称
     * @throws Exception
     */
    public Trigger.TriggerState getJobState(String env, String name) throws Exception {
        if (getScheduler(env).checkExists(JobKey.jobKey(name))) {
            return getScheduler(env).getTriggerState(TriggerKey.triggerKey(name));
        }

        return Trigger.TriggerState.NONE;
    }

    /**
     * 立即运行任务.
     * @throws Exception
     */
    public void triggerJob(String name) throws Exception {
        triggerJob(getEnv(), name);
    }

    @Override
    @Transactional
    public ScheduleJob save(ScheduleJob entity) {
        try {
            if (StringUtils.isBlank(entity.getEnv())) {
                entity.setEnv(env);
            }

            if (entity.getId() != null) {
                ScheduleJob job = getByID(entity.getId());

                if (job != null) {
                    deleteJob(job.getEnv(), job.getName());
                }

                addJob(entity);
            } else {
                if (!getScheduler(entity.getEnv()).checkExists(JobKey.jobKey(entity.getName()))) {
                    addJob(entity);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("添加计划任务失败", e);
        }

        super.save(entity);

        return entity;
    }

    @Override
    protected void afterDelete(ScheduleJob entity) {
        try {
            deleteJob(entity.getEnv(), entity.getName());
        } catch (Exception e) {
            throw new RuntimeException("删除计划任务失败", e);
        }
    }

    /**
     * 向Quartz中添加任务.
     * @param job 要添加的任务实体
     */
    public void addJob(ScheduleJob job) throws Exception {
        if (!StringUtils.isEmpty(job.getCronExpression()) && !StringUtils.isEmpty(job.getTargetBean()) && job.getEnabled() != null && job.getEnabled()) {
            if (!job.getEnv().equals(getEnv())) {
                if (job.getId() != null) {
                    ScheduleJob jobt = getByID(job.getId());

                    if (jobt != null && job.getTargetBean().equals(jobt.getTargetBean())
                            && job.getTargetMethod().equals(jobt.getTargetMethod())
                            && ((job.getCalendarName() == null && jobt.getCalendarName()==null)
                            || (job.getCalendarName() != null && job.getCalendarName().equals(jobt.getCalendarName())))) {
                        getScheduler(job.getEnv()).scheduleJob(buildJobDetail(job), buildTrigger(job));
                        return;
                    }
                }

                // 不是运行环境所在的计划任务交由运行环境处理
                if (StringUtils.isEmpty(job.getTargetMethod())) {
                    job.setTargetMethod(DEFAULT_METHOD);
                }

                if (job.getId() == null) {
                    super.save(job);
                }

                BackgroundJobHelps.addJob(new BackgroundJob(job.getId(),job.getEnv().toLowerCase()) {
                    @Override
                    public void run() {
                        try {
                            // 休眠3秒后再发出请求，防止新增的计划任务还没有持久化
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {}

                        Long jobId = (Long)params[0];
                        String env = (String)params[1];

                        try {
                            //TODO 远程调用服务
//                            JobDubboService jobService = DubboUtils.getBean(JobDubboService.class, env);
                            JobDubboService jobService = null;

                            if (jobService != null) {
                                jobService.callJob(OP.add, jobId);
                            }
                        } catch (Exception e) {
                            logger.warn("计划任务调用其他环境添加任务时发生异常！", e);
                        }
                    }
                });

                return;
            }

            Object targetBean = SpringContextHolder.getBeanNotRequired(job.getTargetBean());

            if (targetBean != null) {
                if (StringUtils.isEmpty(job.getTargetMethod())) {
                    job.setTargetMethod(DEFAULT_METHOD);
                }

                Method jobMethod = null;

                try {
                    try {
                        jobMethod = targetBean.getClass().getMethod(job.getTargetMethod(),ScheduleJob.class);
                    } catch (Exception ex) {
                        jobMethod = targetBean.getClass().getMethod(job.getTargetMethod());
                    }
                } catch (Exception ex) {
                    throw new Exception("在指定的计划任务目标Bean["+targetBean.getClass()+"]中找不到执行方法："+job.getTargetMethod(), ex);
                }

                try {
                    if (jobMethod != null) {
                        if (!StringUtils.isEmpty(job.getCalendarName())) {
                            Calendar calendar = (Calendar)SpringContextHolder.getBeanNotRequired(job.getCalendarName());

                            if (calendar != null) {
                                getScheduler().addCalendar(job.getCalendarName(), calendar, false, false);
                            } else {
                                throw new RuntimeException("指定的工作日历无法从Spring中获取对应的Bean：" + job.getCalendarName());
                            }
                        }

                        getScheduler().scheduleJob(buildJobDetail(job), buildTrigger(job));
                    }
                } catch (Exception ex) {
                    throw new Exception("添加计划["+job.getName()+"]任务失败", ex);
                }
            } else {
                throw new Exception("指定的计划任务目标Bean无效："+job.getTargetBean());
            }
        }
    }

    /**
     * 从Quartz中删除任务.
     * @param env 所属环境
     * @param name 任务名称
     * @throws Exception
     */
    public void deleteJob(String env, String name) throws Exception {
        if (getScheduler(env).checkExists(JobKey.jobKey(name))) {
            getScheduler(env).deleteJob(JobKey.jobKey(name));
        }
    }

    /**
     * 暂停任务.
     * @param env 所属环境
     * @param name 任务名称
     * @throws Exception
     */
    public void pauseJob(String env, String name) throws Exception {
        Assert.notNull(name, "要暂停的任务名称不能为空");

        if (getScheduler(env).checkExists(JobKey.jobKey(name))) {
            getScheduler(env).pauseJob(JobKey.jobKey(name));
        }
    }

    /**
     * 恢复任务.
     * @param env 所属环境
     * @param name 任务名称
     * @throws Exception
     */
    public void resumeJob(String env, String name) throws Exception {
        Assert.notNull(name, "要恢复的任务名称不能为空");
        if (getScheduler(env).checkExists(JobKey.jobKey(name))) {
            getScheduler(env).resumeJob(JobKey.jobKey(name));
        }
    }

    /**
     * 立即运行任务.
     * @param env 所属环境
     * @param name 任务名称
     * @throws Exception
     */
    public void triggerJob(String env, String name) throws Exception {
        Assert.notNull(name, "要运行的任务名称不能为空");
        if (getScheduler(env).checkExists(JobKey.jobKey(name))) {
            getScheduler(env).triggerJob(JobKey.jobKey(name));
        }
    }

    /**
     * 构建计划任务触发器.
     * @param job 任务实体
     * @return 触发器对象
     */
    private CronTrigger buildTrigger(ScheduleJob job) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(job.getName()).withSchedule(scheduleBuilder);

        if (job.getStartTime() != null) {
            triggerBuilder.startAt(job.getStartTime());
        }

        if (job.getEndTime() != null) {
            triggerBuilder.endAt(job.getEndTime());
        }

        triggerBuilder.withPriority(job.getPriority());

        if (!StringUtils.isEmpty(job.getCalendarName())) {
            triggerBuilder.modifiedByCalendar(job.getCalendarName());
        }

        return triggerBuilder.build();
    }

    /**
     * 构建计划任务.
     * @param job 任务实体
     * @return 运行任务对象
     */
    private JobDetail buildJobDetail(ScheduleJob job) {
        JobBuilder jobBuilder = JobBuilder.newJob(job.isConcurrent()?JobFactory.class:StatefulJobFactory.class);
        jobBuilder.storeDurably();
        jobBuilder.requestRecovery(true);
        jobBuilder.withDescription(job.getDescription());
        JobDetail jobDetail = jobBuilder.withIdentity(job.getName()).build();
        jobDetail.getJobDataMap().put("ScheduleJob", job);

        return jobDetail;
    }

    /**
     * 根据环境获取计划任务管理对象.
     * @param env 所属环境
     */
    private Scheduler getScheduler(String env) {
        if (env.equals(getEnv())) {
            return getScheduler();
        }

        return (Scheduler)SpringContextHolder.getBeanNotRequired(env+"_JobsFactoryBean");
    }

    /**
     * 获取Quartz计划任务管理对象.
     */
    private Scheduler getScheduler() {
        if (scheduler == null) {
            scheduler = (Scheduler) SpringContextHolder.getBeanNotRequired("JobsFactoryBean");

            if (scheduler != null) {
                try {
                    env = scheduler.getSchedulerName();
                } catch (SchedulerException e) {
                    throw new RuntimeException("无法获取计划任务运行环境", e);
                }
            }else{
                logger.warn("无法获取计划任务管理对象！");
            }
        }

        return scheduler;
    }

    /**
     * 获取当前环境.
     */
    private String getEnv() {
        if (env==null) {
            getScheduler();
        }

        if (StringUtils.isBlank(env)) {
            throw new RuntimeException("无法获取计划任务所属环境");
        }

        return env;
    }

    /** 默认的计划任务执行方法名. */
    private static final String DEFAULT_METHOD = "doJob";
    /** Quartz计划任务管理对象. */
    private Scheduler scheduler;
    /** 所属环境. */
    private String env;

    @Override
    protected JobDao getDao() {
        return dao;
    }

    @Autowired
    private JobDao dao;
}
