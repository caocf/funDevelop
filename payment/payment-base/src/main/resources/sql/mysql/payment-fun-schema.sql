drop table if exists `fun_payment_reconciliation`;
drop table if exists `fun_payment_orderno`;
drop table if exists `fun_payment_refundorderno`;
drop table if exists `fun_payment_notify_record`;
drop table if exists `fun_payment_refunding`;
commit;

CREATE TABLE `fun_payment_orderno` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `HIGHT_CODE` varchar(4) DEFAULT NULL COMMENT '高位码',
  `LOW_CODE` varchar(50) NOT NULL COMMENT '低位码',
  `ORDER_NO` varchar(100) DEFAULT NULL COMMENT '订单号',
  `ORDER_TYPE` varchar(100) NOT NULL COMMENT '订单类型',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_ORDER_NO` (`ORDER_NO`),
  KEY `IDX_ORDER_TYPE` (`ORDER_TYPE`)
) COMMENT='订单号';

commit;

CREATE TABLE `fun_payment_refundorderno` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `HIGHT_CODE` varchar(4) DEFAULT NULL COMMENT '高位码',
  `LOW_CODE` varchar(50) NOT NULL COMMENT '低位码',
  `ORDER_NO` varchar(100) DEFAULT NULL COMMENT '订单号',
  `ORDER_TYPE` varchar(100) NOT NULL COMMENT '订单类型',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_ORDER_NO` (`ORDER_NO`),
  KEY `IDX_ORDER_TYPE` (`ORDER_TYPE`)
) COMMENT='退款订单号';

commit;

CREATE TABLE `fun_payment_notify_record` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORDER_NO` varchar(64) DEFAULT NULL COMMENT '订单号',
  `ORDER_TYPE` varchar(100) NOT NULL COMMENT '订单类型',
  `PLATFORM_ORDER_NO` varchar(64) DEFAULT NULL COMMENT '支付平台订单号',
  `PLATFORM` char(5) NOT NULL COMMENT '支付方式(1:微信,2:支付宝,3:快钱,4:联动U付)',
  `NOTIFY_TIME` datetime NOT NULL COMMENT '通知时间',
  `NOTIFY_TYPE` varchar(64) DEFAULT NULL COMMENT '通知类型',
  `NOTIFY_CONTENT` varchar(2000) NOT NULL COMMENT '通知报文',
  `RETURN_CONTENT` varchar(100) DEFAULT NULL COMMENT '服务器响应内容',
  PRIMARY KEY (`ID`)
) COMMENT='支付平台通知记录';

commit;

CREATE TABLE `fun_payment_reconciliation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORDER_TYPE` varchar(100) NOT NULL COMMENT '订单类型',
  `ORDER_NO` varchar(100) NOT NULL COMMENT '订单编号',
  `ORDER_PRODUCT_NAME` varchar(256) DEFAULT NULL COMMENT '商品名称',
  `PAY_STATUS` char(2) NOT NULL COMMENT '订单支付状态(1:等待支付,2:已支付,3:取消,4:过期)',
  `ORDER_AMOUNT` bigint(20) NOT NULL COMMENT '订单金额（单位分）',
  `ORDER_PAY_AMOUNT` bigint(20) NULL COMMENT '支付金额（单位分）',
  `ORDER_PAY_TIME` datetime DEFAULT NULL COMMENT '支付时间',
  `ORDER_CREATE_TIME` datetime NOT NULL COMMENT '首次发起支付时间',
  `PLATFORM` char(5) DEFAULT NULL COMMENT '支付平台(1:微信,2:支付宝,3:快钱,4:联动U付)',
  `PAY_SERIAL_NUMBER` varchar(100) DEFAULT NULL COMMENT '平台支付流水号',
  `CHECK_TIME` datetime DEFAULT NULL COMMENT '对账时间',
  `CHECK_STATUS` char(2) DEFAULT NULL COMMENT '对账状态(1:未对账,2:对账成功,3:对账失败,4:未能核实,5:手工核实)',
  `CHECK_RESULT` varchar(256) DEFAULT NULL COMMENT '对账结果',
  PRIMARY KEY (`ID`),
  KEY `IDX_ORDER_TYPE` (`ORDER_TYPE`),
  KEY `IDX_ORDER_NO` (`ORDER_NO`),
  KEY `IDX_ORDER_STATUS` (`PAY_STATUS`),
  KEY `IDX_PLATFORM` (`PLATFORM`),
  KEY `IDX_PAY_SERIAL_NUMBER` (`PAY_SERIAL_NUMBER`),
  KEY `IDX_CHECK_STATUS` (`CHECK_STATUS`)
) COMMENT='存放支付信息';

commit;

CREATE TABLE `fun_payment_refunding` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORDER_TYPE` varchar(100) NOT NULL COMMENT '订单类型',
  `ORDER_NO` varchar(100) NOT NULL COMMENT '订单编号',
  `ORDER_PRODUCT_NAME` varchar(256) NOT NULL COMMENT '商品名称',
  `REFUND_BATCH_CODE` varchar(100) DEFAULT NULL COMMENT '退款批次号',
  `REFUND_AMOUNT` bigint(20) NOT NULL COMMENT '退款金额（单位分）',
  `REALLY_REFUND_AMOUNT` bigint(20) DEFAULT NULL COMMENT '实际退款金额单位分）',
  `REFUND_REASON` varchar(1000) DEFAULT NULL COMMENT '退款原因',
  `REFUND_STATUS` char(1) DEFAULT NULL COMMENT '退款状态(1:等待退款,2:已退款,3:取消)',
  `REFUND_APPLY_TIME` datetime NOT NULL COMMENT '退款申请时间',
  `PLATFORM_RESPONSE_TIME` datetime DEFAULT NULL COMMENT '退款成功时间',
  `RECEIVE_NAME` varchar(64) DEFAULT NULL COMMENT '收款方名称',
  `RECEIVE_BANK_NAME` varchar(64) DEFAULT NULL COMMENT '收款方银行名称',
  `RECEIVE_BANK_NUMBER` varchar(64) DEFAULT NULL COMMENT '收款方银行账号',
  `USER_REFUND_DATE` datetime DEFAULT NULL COMMENT '客户申请退款时间',
  `PAYMENT_ORDER_NO` varchar(100) DEFAULT NULL COMMENT '原支付订单号',
  `PAYMENT_PAY_TIME` datetime DEFAULT NULL COMMENT '支付成功时间',
  `PAYMENT_PAY_AMOUNT` bigint(20) DEFAULT NULL COMMENT '原订单支付金额（单位分）',
  `PAYMENT_PLATFORM` char(5) NOT NULL COMMENT '原支付平台(1:支付宝,2:微信,3:快钱,4:联动U付)',
  `PAYMENT_PLATFORM_SERIAL_NUMBER` varchar(100) NOT NULL COMMENT '平台支付流水号',
  `CHECK_TIME` datetime DEFAULT NULL COMMENT '对账时间',
  `CHECK_STATUS` char(2) DEFAULT NULL COMMENT '对账状态(1:未对账,2:对账成功,3:对账失败,4:未能核实,5:手工核实)',
  `CHECK_RESULT` varchar(256) DEFAULT NULL COMMENT '对账结果',
  PRIMARY KEY (`ID`),
  KEY `IDX_ORDER_TYPE` (`ORDER_TYPE`),
  KEY `IDX_ORDER_NO` (`ORDER_NO`),
  KEY `IDX_REFUND_STATUS` (`REFUND_STATUS`),
  KEY `IDX_PAYMENT_ORDER_NO` (`PAYMENT_ORDER_NO`),
  KEY `IDX_PAYMENT_PLATFORM_SERIAL_NUMBER` (`PAYMENT_PLATFORM_SERIAL_NUMBER`),
  KEY `IDX_CHECK_STATUS` (`CHECK_STATUS`)
) COMMENT='存放退款信息';

commit;


