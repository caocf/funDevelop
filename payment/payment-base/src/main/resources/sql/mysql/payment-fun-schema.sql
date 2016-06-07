drop table if exists `fun_fin_reconciliation`;
drop table if exists `fun_sys_orderno`;
drop table if exists `fun_fin_payment_notify_record`;
commit;

CREATE TABLE `fun_fin_reconciliation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORDER_TYPE` char(1) NOT NULL COMMENT '订单类型',
  `ORDER_NO` varchar(100) NOT NULL COMMENT '订单编号',
  `ORDER_ID` bigint(20) NOT NULL COMMENT '订单ID',
  `ORDER_PRODUCT_NAME` varchar(256) DEFAULT NULL COMMENT '商品名称',
  `PAY_STATUS` char(2) NOT NULL COMMENT '订单支付状态',
  `ORDER_AMOUNT` bigint(20) NOT NULL COMMENT '支付金额（单位分）',
  `ORDER_PAY_TIME` datetime DEFAULT NULL COMMENT '支付时间',
  `ORDER_CREATE_TIME` datetime NOT NULL COMMENT '下单时间',
  `PLATFORM` char(2) DEFAULT NULL COMMENT '支付平台(1:微信,2:支付宝,3:快钱,4:联动U付)',
  `USER_ID` bigint(20) NOT NULL COMMENT '支付用户ID',
  `TARGET_USER_ID` bigint(20) NOT NULL COMMENT '收费用户ID',
  `PAY_SERIAL_NUMBER` varchar(100) DEFAULT NULL COMMENT '平台支付流水号',
  `REFUND_TIME` datetime DEFAULT NULL COMMENT '退款时间',
  `REFUND_AMOUNT` bigint(20) DEFAULT NULL COMMENT '退款金额（单位分）',
  `REFUND_SERIAL_NUMBER` varchar(100) DEFAULT NULL COMMENT '退款流水号',
  `CHECK_TIME` datetime DEFAULT NULL COMMENT '对账时间',
  `CHECK_STATUS` char(2) DEFAULT NULL COMMENT '对账状态',
  `CHECK_RESULT` varchar(256) DEFAULT NULL COMMENT '对账结果',
  PRIMARY KEY (`ID`),
  KEY `IDX_TARGET_USER_ID` (`TARGET_USER_ID`),
  KEY `IDX_ORDER_TYPE` (`ORDER_TYPE`),
  KEY `IDX_ORDER_ID` (`ORDER_ID`),
  KEY `IDX_ORDER_NUMBER` (`ORDER_NO`),
  KEY `IDX_ORDER_NO` (`ORDER_ID`),
  KEY `IDX_ORDER_STATUS` (`PAY_STATUS`)
) COMMENT='存放财务对账信息';

commit;

CREATE TABLE `fun_sys_orderno` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `HIGHT_CODE` varchar(4) DEFAULT NULL COMMENT '高位码',
  `LOW_CODE` varchar(50) NOT NULL COMMENT '低位码',
  `ORDER_NO` varchar(100) DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_ORDER_NO` (`ORDER_NO`)
) COMMENT='订单号';

commit;

CREATE TABLE `fun_fin_payment_notify_record` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORDER_NO` varchar(64) DEFAULT NULL COMMENT '订单号',
  `ORDER_TYPE` char(1) NOT NULL,
  `PLATFORM_ORDER_NO` varchar(64) DEFAULT NULL COMMENT '支付平台订单号',
  `PAYMENT_MODE` varchar(20) NOT NULL COMMENT '支付方式(1:微信,2:支付宝,3:快钱,4:联动U付)',
  `NOTIFY_TIME` datetime NOT NULL COMMENT '通知时间',
  `NOTIFY_TYPE` varchar(64) DEFAULT NULL COMMENT '通知类型',
  `NOTIFY_CONTENT` varchar(2000) NOT NULL COMMENT '通知报文',
  `RETURN_CONTENT` varchar(100) DEFAULT NULL COMMENT '服务器响应内容',
  PRIMARY KEY (`ID`)
) COMMENT='支付通知记录';

commit;
