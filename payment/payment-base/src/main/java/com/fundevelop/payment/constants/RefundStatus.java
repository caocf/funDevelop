package com.fundevelop.payment.constants;

/**
 * 退款状态枚举定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 12:25
 */
public enum RefundStatus {
    等待退款(1),
    已退款(2),
    取消(3);

    private int code;

    RefundStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code+"";
    }

    public static RefundStatus getByCode(String code) {
        return getByCode(Integer.parseInt(code, 10));
    }

    public static RefundStatus getByCode(int code) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.code==code) {
                return status;
            }
        }

        return null;
    }
}
