package com.fundevelop.payment.service;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.payment.base.PaymentContentCreator;
import com.fundevelop.payment.base.PaymentInfo;
import com.fundevelop.payment.constants.CheckStatus;
import com.fundevelop.payment.constants.PayStatus;
import com.fundevelop.payment.constants.PaymentPlatform;
import com.fundevelop.payment.entity.ReconciliationEntity;
import com.fundevelop.payment.manager.ReconciliationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 支付服务管理类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 13:08
 */
@Component
public class PaymentService {
    /**
     * 构造支付平台相关信息.
     * @param platform 支付平台
     * @param paymentInfo 支付信息
     */
    @Transactional
    public Object buildContent(PaymentPlatform platform, PaymentInfo paymentInfo) {
        Assert.notNull(platform , "支付平台不能为null");
        Assert.notNull(paymentInfo , "支付信息不能为null");
        Assert.hasLength(paymentInfo.getOrderType() , "订单类型不能为空");
        Assert.hasLength(paymentInfo.getOrderNo() , "订单编号不能为空");
        Assert.hasLength(paymentInfo.getProductName() , "商品名称不能为空");
        Assert.notNull(paymentInfo.getOrderAmount() , "订单金额不能为null");

        ReconciliationEntity reconciliationEntity = reconciliationManager.getByOrderNo(paymentInfo.getOrderNo());

        if (reconciliationEntity == null) {
            reconciliationEntity = new ReconciliationEntity();
            reconciliationEntity.setOrderType(paymentInfo.getOrderType());
            reconciliationEntity.setOrderNo(paymentInfo.getOrderNo());
            reconciliationEntity.setOrderProductName(paymentInfo.getProductName());
            reconciliationEntity.setPayStatus(PayStatus.等待支付.getCode()+"");
            reconciliationEntity.setOrderAmount(paymentInfo.getOrderAmount());
            reconciliationEntity.setOrderCreateTime(new Date());
            reconciliationEntity.setPlatform(platform.getCode()+"");
            reconciliationEntity.setCheckStatus(CheckStatus.未对账.toString());

            reconciliationManager.save(reconciliationEntity);
        } else {
            PayStatus payStatus = PayStatus.getByCode(reconciliationEntity.getPayStatus());

            if (payStatus != PayStatus.等待支付) {
                logger.warn("生成支付报文:订单:{} 状态不是等待支付(为:{})不能支付", paymentInfo.getOrderNo(), payStatus);
                throw new RuntimeException("订单状态不是等待支付(为:"+payStatus+")不能支付");
            }

            if (!(platform.getCode()+"").equals(reconciliationEntity.getPlatform())) {
                reconciliationEntity.setPlatform(platform.getCode()+"");
                reconciliationManager.save(reconciliationEntity);
            }
        }

        return getContentCreator(platform).buildContent(paymentInfo);
    }

    /**
     * 完成支付.
     * @param platform 支付平台
     * @param orderNo 订单支付单号
     * @param orderAmount 支付金额
     * @param paySerialNumber 平台支付流水号
     */
    @Transactional
    public void completePay(PaymentPlatform platform, String orderNo, Integer orderAmount, String paySerialNumber) {
        Assert.notNull(platform , "支付平台不能为null");
        Assert.hasLength(orderNo , "订单号不能为空");
        Assert.notNull(orderAmount , "支付金额不能为空");
        Assert.hasLength(paySerialNumber , "平台支付流水号不能为空");

        ReconciliationEntity reconciliationEntity = reconciliationManager.getByOrderNo(orderNo);

        if (reconciliationEntity == null) {
            throw new RuntimeException("没有找到订单号:("+orderNo+")对应的支付信息");
        }

        PayStatus payStatus = PayStatus.getByCode(reconciliationEntity.getPayStatus());

        if (payStatus == PayStatus.已支付) {
            logger.warn("完成支付:订单:(){}已经支付,不在更新数据。", orderNo);
            return;
        }

        reconciliationEntity.setPlatform(platform.getCode()+"");
        reconciliationEntity.setOrderPayAmount(orderAmount);
        reconciliationEntity.setPaySerialNumber(paySerialNumber);

        reconciliationManager.save(reconciliationEntity);

        Collection<PaymentEventHandler> eventHandlers = getEventHandlers();

        if (eventHandlers != null && !eventHandlers.isEmpty()) {
            for (PaymentEventHandler eventHandler : eventHandlers) {
                eventHandler.completePay(platform, orderNo, orderAmount, paySerialNumber);
            }
        }
    }

    private Collection<PaymentEventHandler> getEventHandlers() {
        Map<String, PaymentEventHandler> eventHandlerMap = SpringContextHolder.getBeans(PaymentEventHandler.class);

        if (eventHandlerMap != null && !eventHandlerMap.isEmpty()) {
            return eventHandlerMap.values();
        }

        return null;
    }

    private PaymentContentCreator getContentCreator(PaymentPlatform platform) {
        PaymentContentCreator contentCreator = (PaymentContentCreator)SpringContextHolder.getBeanNotRequired(CONTENT_CREATOR_BEAN_NAME+platform.getCode());

        if (contentCreator == null) {
            logger.error("无法获取支付平台({})支付信息构造类", platform);
            throw new RuntimeException("无法获取"+platform+"支付信息构造类");
        }

        return contentCreator;
    }

    @Autowired
    private ReconciliationManager reconciliationManager;

    /** 支付平台支付信息构造类Bean名称前缀. */
    private final static String CONTENT_CREATOR_BEAN_NAME = "fun_paymentContentCreator_";
    private Logger logger = LoggerFactory.getLogger(getClass());
}
