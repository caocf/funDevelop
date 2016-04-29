package com.fundevelop.job.quartz;

import org.quartz.DisallowConcurrentExecution;

/**
 * 计划任务运行工厂类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:38
 */
@DisallowConcurrentExecution
public class StatefulJobFactory extends JobFactory {
}
