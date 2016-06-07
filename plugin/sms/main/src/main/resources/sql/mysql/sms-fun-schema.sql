drop table if exists `fun_sms`;
commit;

CREATE TABLE `fun_sms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `country_code` varchar(5) NOT NULL DEFAULT '86' COMMENT '国家代码',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号码',
  `content` varchar(500) NOT NULL COMMENT '短信内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '定时发送时间',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '发送状态(1:未发送，2:已发送)',
  `priority` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '优先级',
  `send_timing` datetime DEFAULT NULL COMMENT '实际发送时间',
  `system` varchar(50) DEFAULT NULL COMMENT '业务系统',
  `module` varchar(50) DEFAULT NULL COMMENT '业务模块',
  `memo` varchar(100) DEFAULT NULL COMMENT '备注',
  `rrid` varchar(128) DEFAULT NULL COMMENT 'RRID',
  `service_code` varchar(10) DEFAULT NULL COMMENT '特服号',
  `response_status` varchar(20) DEFAULT NULL COMMENT '响应发送状态',
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `channel` varchar(10) DEFAULT NULL COMMENT '短信通道',
  PRIMARY KEY (`id`),
  KEY `ind_sys_sms` (`rrid`),
  KEY `ix_status` (`status`),
  KEY `ix_system` (`system`),
  KEY `ix_module` (`module`),
  KEY `ix_phone` (`phone`),
  KEY `ix_priority` (`priority`),
  KEY `ix_create_time` (`create_time`),
  KEY `ix_send_timing` (`send_timing`),
  KEY `ix_send_time` (`send_time`),
  KEY `ix_channel` (`channel`)
) COMMENT='短信发送';

commit;
