package com.fundevelop.job.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.job.quartz.manager.JobService;
import com.fundevelop.persistence.entity.hibernate.LongIDEntity;
import org.quartz.Trigger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 计划任务实体类.
 * <p>描述:存储计划任务信息</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:28
 */
@Entity
@Table(name = "fun_jobs")
public class ScheduleJob extends LongIDEntity implements Serializable {
    /** 任务名称 **/
    private String name;
    /** 运行环境. */
    private String env;
    /** 任务是否允许并发执行. */
    private boolean concurrent;
    /** 任务运行时间表达式 **/
    private String cronExpression;
    /** 任务描述 **/
    private String description;
    /** 任务对应的Bean名称. */
    private String targetBean;
    /** 任务对应的方法. */
    private String targetMethod;
    /** 开始时间. */
    private Date startTime;
    /** 结束时间. */
    private Date endTime;
    /** 优先级. */
    private int priority;
    /** 日历名称. */
    private String calendarName;
    /** 是否有效. */
    private Boolean enabled;
    /** 任务参数 */
    private String param;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    @Transient
    public String getStatusDesc() {
        try {
            JobService jobService = SpringContextHolder.getBean(JobService.class);
            Trigger.TriggerState status = jobService.getJobState(getEnv(), getName());
            switch (status) {
                case NONE:
                    return "停止";
                case NORMAL:
                    return "正常";
                case PAUSED:
                    return "暂停";
                case COMPLETE:
                    return "完成";
                case BLOCKED:
                    return "线程阻塞";
                case ERROR:
                    return "错误";
                default:
                    return "未知";
            }
        } catch (Exception ex) {
            return "获取失败";
        }
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(String targetBean) {
        this.targetBean = targetBean;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
