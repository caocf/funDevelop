package com.fundevelop.payment.entity;

import com.fundevelop.persistence.entity.hibernate.LongIDEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订单号实体.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/27 0:44
 */
@Entity
@Table(name = "fun_sys_orderno")
public class OrderNoEntity extends LongIDEntity {
    /** 高位码 */
    private String hightCode;
    /** 低位码 */
    private String lowCode;
    /** 订单号 */
    private String orderNo;

    public String getHightCode() {
        return hightCode;
    }

    public void setHightCode(String hightCode) {
        this.hightCode = hightCode;
    }

    public String getLowCode() {
        return lowCode;
    }

    public void setLowCode(String lowCode) {
        this.lowCode = lowCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
