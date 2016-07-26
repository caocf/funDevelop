package com.fundevelop.payment.utils;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.payment.manager.OrderNoManager;
import org.springframework.util.Assert;

/**
 * 订单号工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 12:30
 */
public class OrderNoUtils {
    /**
     * 获取订单号前缀.
     * 由“系统环境首字母”+“_”组成，
     * 如果是生产环境则返回空
     */
    public static String getOrderNoEnvPrefix() {
        String prefix = "";
        String env = PropertyUtil.get("service.env");

        if (!"product".equalsIgnoreCase(env)) {
            prefix = env.substring(0,1).toUpperCase() + "_";
        }

        return prefix;
    }

    /**
     * 获取订单号.
     * @param orderType 订单类型
     */
    public static String getOrderNo(String orderType) {
        return getOrderNo(orderType, null);
    }

    /**
     * 获取订单号.
     * @param orderType 订单类型
     * @param pre 订单号前缀
     * @param pre 订单号前缀
     */
    public static String getOrderNo(String orderType, String pre) {
        Assert.hasLength(orderType , "订单类型不能为空");

        OrderNoManager orderNoManager = SpringContextHolder.getBean(OrderNoManager.class);
        int length = Integer.parseInt(PropertyUtil.get("payment.orderNo.length", "10"), 10);

        return orderNoManager.createOrderNo(orderType, pre, length);
    }

    private OrderNoUtils(){}
}
