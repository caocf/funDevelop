package com.fundevelop.payment.entity;

import com.fundevelop.persistence.entity.hibernate.LongIDEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 支付平台通知记录实体.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 2:07
 */
@Entity
@Table(name = "fun_payment_notify_record")
public class PaymentNotifyRecordEntity extends LongIDEntity {
    /** 订单类型 */
    private String orderType;
    /** 支付平台的订单号码 */
    private String platformOrderNo;
    /** 订单编号 */
    private String orderNo;
    /** 支付平台. */
    private String platform;
    /** 通知类型 */
    private String notifyType;
    /** 通知时间 */
    private Date notifyTime;
    /** 通知内容（报文，建议用JSON格式保存） */
    private String notifyContent;
    /** 服务器响应内容 */
    private String returnContent;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPlatformOrderNo() {
        return platformOrderNo;
    }

    public void setPlatformOrderNo(String platformOrderNo) {
        this.platformOrderNo = platformOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public String getReturnContent() {
        return returnContent;
    }

    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
    }
}
