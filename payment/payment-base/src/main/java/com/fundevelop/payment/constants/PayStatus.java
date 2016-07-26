package com.fundevelop.payment.constants;

/**
 * 支付状态枚举定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 12:21
 */
public enum PayStatus {
    等待支付(1),
    已支付(2),
    取消(3),
    过期(4);

    private int code;

    PayStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PayStatus getByCode(String code) {
        return getByCode(Integer.parseInt(code, 10));
    }

    public static PayStatus getByCode(int code) {
        for (PayStatus status : PayStatus.values()) {
            if (status.code==code) {
                return status;
            }
        }

        return null;
    }
}
