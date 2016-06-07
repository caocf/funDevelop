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
@Table(name = "fun_fin_reconciliation")
public class ReconciliationEntity extends LongIDEntity {
    /** 订单类型. */
    private String orderType;
    /** 订单编号. */
    private String orderNo;
    /** 订单ID. */
    private Long orderId;
    /** 商品名称. */
    private String orderProductName;
    /** 订单支付状态. */
    private String payStatus;
    /** 支付金额. */
    private Long orderAmount;
    /** 支付时间. */
    private Date orderPayTime;
    /** 下单时间. */
    private Date orderCreateTime;
    /** 支付平台. */
    private String platform;
    /** 支付用户ID. */
    private Long userId;
    /** 提供服务用户ID */
    private Long targetUserId;
    /** 平台支付流水号. */
    private String paySerialNumber;
    /** 退款时间. */
    private Date refundTime;
    /** 退款金额. */
    private Double refundAmount;
    /** 退款流水号. */
    private String refundSerialNumber;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getPaySerialNumber() {
        return paySerialNumber;
    }

    public void setPaySerialNumber(String paySerialNumber) {
        this.paySerialNumber = paySerialNumber;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundSerialNumber() {
        return refundSerialNumber;
    }

    public void setRefundSerialNumber(String refundSerialNumber) {
        this.refundSerialNumber = refundSerialNumber;
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
