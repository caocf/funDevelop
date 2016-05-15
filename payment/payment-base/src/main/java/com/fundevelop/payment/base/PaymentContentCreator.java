package com.fundevelop.payment.base;

/**
 * 支付平台支付信息构造接口定义类.
 * <p>描述:负责生成平台相关信息</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/11 22:40
 */
public interface PaymentContentCreator {
    /**
     * 构造支付平台相关信息.
     * @param paymentInfo 支付信息
     */
    Object buildContent(PaymentInfo paymentInfo);
}
