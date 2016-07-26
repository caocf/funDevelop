package com.fundevelop.payment.service;

import com.fundevelop.payment.constants.PaymentPlatform;

/**
 * 支付事件处理器接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 17:58
 */
public interface PaymentEventHandler {
    /**
     * 完成支付.
     * @param platform 支付平台
     * @param orderNo 订单支付单号
     * @param orderAmount 支付金额
     * @param paySerialNumber 平台支付流水号
     */
    void completePay(PaymentPlatform platform, String orderNo, Integer orderAmount, String paySerialNumber);
}
