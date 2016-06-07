drop table if exists `fun_jobs`;
commit;

CREATE TABLE `fun_jobs` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `NAME` varchar(200) NOT NULL COMMENT '任务名称',
  `ENV` varchar(200) NOT NULL COMMENT '运行环境',
  `CONCURRENT` char(1) NOT NULL COMMENT '是否允许并发执行',
  `CRON_EXPRESSION` varchar(200) DEFAULT NULL COMMENT '运行时间表达式',
  `DESCRIPTION` varchar(250) DEFAULT NULL COMMENT '任务描述',
  `TARGET_BEAN` varchar(250) DEFAULT NULL COMMENT '目标Bean名称',
  `TARGET_METHOD` varchar(250) DEFAULT NULL COMMENT '目标方法',
  `START_TIME` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `END_TIME` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `PRIORITY` decimal(13,0) DEFAULT NULL COMMENT '优先级',
  `CALENDAR_NAME` varchar(200) DEFAULT NULL COMMENT '日历名称',
  `ENABLED` char(1) NOT NULL COMMENT '是否有效',
  `PARAM` varchar(4000) DEFAULT NULL COMMENT '任务参数',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `idx_fun_jobs` (`NAME`,`ENV`)
) COMMENT='计划任务信息表';

commit;
