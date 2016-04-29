package com.fundevelop.job.quartz.rpc;

import com.fundevelop.job.quartz.manager.JobService;

/**
 * 计划任务Dubbo服务接口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:40
 */
public interface JobDubboService {
    /**
     * 调用计划任务.
     * @param op 操作指令
     * @param id 任务ID
     */
    public String callJob(JobService.OP op, Long id);
}
