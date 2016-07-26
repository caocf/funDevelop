package com.fundevelop.payment.manager;

import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.payment.dao.PaymentNotifyRecordDao;
import com.fundevelop.payment.entity.PaymentNotifyRecordEntity;
import com.fundevelop.payment.entity.ReconciliationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 支付平台通知记录管理类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 2:12
 */
@Component
public class PaymentNotifyRecordManager extends AbstractManager<PaymentNotifyRecordEntity, Long, PaymentNotifyRecordDao> {
    /**
     * 记录支付平台的通知
     * @param orderNo 订单编号
     * @param platformOrderNo 支付平台编号
     * @param platform 支付方式
     * @param notifyType 通知类型
     * @param notifyContent 通知内容
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public PaymentNotifyRecordEntity recordNotify(String orderNo, String platformOrderNo,String platform, String notifyType,
                                                  String notifyContent) {

        PaymentNotifyRecordEntity recordEntity = new PaymentNotifyRecordEntity();

        recordEntity.setPlatformOrderNo(platformOrderNo);
        recordEntity.setOrderNo(orderNo);
        recordEntity.setPlatform(platform);
        recordEntity.setNotifyType(notifyType);
        recordEntity.setNotifyContent(notifyContent);
        recordEntity.setNotifyTime(new Date());

        ReconciliationEntity reconciliationEntity = reconciliationManager.getByOrderNo(orderNo);

        if (reconciliationEntity == null) {
            recordEntity.setOrderType("0");
        } else {
            recordEntity.setOrderType(reconciliationEntity.getOrderType());
        }

        save(recordEntity);

        return recordEntity;
    }

    @Autowired
    private ReconciliationManager reconciliationManager;

    @Override
    protected PaymentNotifyRecordDao getDao() {
        return dao;
    }

    @Autowired
    private PaymentNotifyRecordDao dao;
}
