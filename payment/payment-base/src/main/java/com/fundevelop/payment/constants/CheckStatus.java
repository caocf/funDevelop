package com.fundevelop.payment.constants;

/**
 * 订单对账状态枚举定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 12:23
 */
public enum CheckStatus {
    未对账(1),
    对账成功(2),
    对账失败(3),
    未能核实(4),
    手工核实(5);

    private int code;

    CheckStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code+"";
    }

    public static CheckStatus getByCode(String code) {
        return getByCode(Integer.parseInt(code, 10));
    }

    public static CheckStatus getByCode(int code) {
        for (CheckStatus status : CheckStatus.values()) {
            if (status.code==code) {
                return status;
            }
        }

        return null;
    }
}
