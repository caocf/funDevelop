package com.fundevelop.job.quartz.dao;

import com.fundevelop.job.quartz.entity.ScheduleJob;
import com.fundevelop.persistence.jpa.BaseDao;

/**
 * 计划任务Dao.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:45
 */
public interface JobDao extends BaseDao<ScheduleJob, Long> {
    /**
     * 根据任务所属环境及任务名称获取任务.
     */
    public ScheduleJob getByEnvAndName(String env, String name);
}
