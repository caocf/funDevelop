package com.fundevelop.payment.constants;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.payment.base.PaymentInfo;
import com.fundevelop.payment.service.PaymentService;

/**
 * 支付平台枚举类型定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 12:28
 */
public enum PaymentPlatform {
    微信(1),
    支付宝(2),
    快钱(3),
    联动U付(4);

    private int code;

    PaymentPlatform(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PaymentPlatform getByCode(String code) {
        return getByCode(Integer.parseInt(code, 10));
    }

    public static PaymentPlatform getByCode(int code) {
        for (PaymentPlatform platform : PaymentPlatform.values()) {
            if (platform.code==code) {
                return platform;
            }
        }

        return null;
    }

    /**
     * 构造支付平台相关信息.
     * @param paymentInfo 支付信息
     */
    public Object buildContent(PaymentInfo paymentInfo) {
        PaymentService service = SpringContextHolder.getBean(PaymentService.class);

        return service.buildContent(this, paymentInfo);
    }
}
