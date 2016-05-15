package com.fundevelop.payment.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付需要的业务信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/11 22:44
 */
public class PaymentInfo implements Serializable {
    /** 订单金额以分为单位. */
    private int orderAmount;
    /** 商品名称. */
    private String productName;
    /** 客户端IP地址. */
    private String ip;
    /** 补充说明. */
    private String desc;
    /** 订单号. */
    private String orderNo;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
