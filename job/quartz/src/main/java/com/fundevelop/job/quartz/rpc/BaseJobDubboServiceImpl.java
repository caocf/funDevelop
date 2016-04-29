package com.fundevelop.job.quartz.rpc;

import com.fundevelop.job.quartz.entity.ScheduleJob;
import com.fundevelop.job.quartz.manager.JobService;

/**
 * 计划任务Dubbo服务接口默认实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:41
 */
public abstract class BaseJobDubboServiceImpl implements JobDubboService {
    /**
     * 获取计划任务管理服务类实例.
     */
    public abstract JobService getJobService();

    @Override
    public String callJob(JobService.OP op, Long id) {
        ScheduleJob job = getJobService().getByID(id);

        if (job != null) {
            try {
                switch (op) {
                    case add:
                        getJobService().addJob(job);
                        break;
                    case pause:
                        getJobService().pauseJob(job.getEnv(), job.getName());
                        break;
                    case resume:
                        getJobService().resumeJob(job.getEnv(), job.getName());
                        break;
                    case runOnce:
                        getJobService().triggerJob(job.getEnv(), job.getName());
                        break;
                    case cancel:
                        getJobService().deleteJob(job.getEnv(), job.getName());
                        break;
                    case getRunJobs:
                        break;
                    default:
                        return "无效指令:"+op;
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "找不到任务："+id;
        }

        return "";
    }
}
