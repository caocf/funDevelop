package com.fundevelop.payment.utils;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.payment.constants.PaymentPlatform;
import com.fundevelop.payment.service.PaymentService;

/**
 * 支付工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 17:23
 */
public class PaymentUtils {
    /**
     * 完成支付.
     * @param platform 支付平台
     * @param orderNo 订单支付单号
     * @param orderAmount 支付金额
     * @param paySerialNumber 平台支付流水号
     */
    public static void completePay(PaymentPlatform platform, String orderNo, Integer orderAmount, String paySerialNumber) {
        PaymentService service = SpringContextHolder.getBean(PaymentService.class);
        service.completePay(platform, orderNo, orderAmount, paySerialNumber);
    }

    private PaymentUtils(){}
}
