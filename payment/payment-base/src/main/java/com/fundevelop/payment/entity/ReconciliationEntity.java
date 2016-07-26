package com.fundevelop.payment.entity;

import com.fundevelop.persistence.entity.hibernate.LongIDEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 财务对账信息表实体类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 1:17
 */
@Entity
@Table(name = "fun_payment_reconciliation")
public class ReconciliationEntity extends LongIDEntity {
    /** 订单类型. */
    private String orderType;
    /** 订单编号. */
    private String orderNo;
    /** 商品名称. */
    private String orderProductName;
    /** 订单支付状态(1:等待支付,2:已支付,3:取消,4:过期). */
    private String payStatus;
    /** 订单金额（单位分）. */
    private Integer orderAmount;
    /** 支付金额（单位分）. */
    private Integer orderPayAmount;
    /** 支付时间. */
    private Date orderPayTime;
    /** 首次发起支付时间. */
    private Date orderCreateTime;
    /** 支付平台. */
    private String platform;
    /** 平台支付流水号. */
    private String paySerialNumber;
    /** 对账时间. */
    private Date checkTime;
    /** 对账状态. */
    private String checkStatus;
    /** 对账结果. */
    private String checkResult;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderProductName() {
        return orderProductName;
    }

    public void setOrderProductName(String orderProductName) {
        this.orderProductName = orderProductName;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getOrderPayAmount() {
        return orderPayAmount;
    }

    public void setOrderPayAmount(Integer orderPayAmount) {
        this.orderPayAmount = orderPayAmount;
    }

    public Date getOrderPayTime() {
        return orderPayTime;
    }

    public void setOrderPayTime(Date orderPayTime) {
        this.orderPayTime = orderPayTime;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPaySerialNumber() {
        return paySerialNumber;
    }

    public void setPaySerialNumber(String paySerialNumber) {
        this.paySerialNumber = paySerialNumber;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }
}
