package com.fundevelop.payment.manager;

import com.fundevelop.commons.utils.DateUtils;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.payment.dao.OrderNoDao;
import com.fundevelop.payment.entity.OrderNoEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 订单号管理类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/27 0:47
 */
@Component
public class OrderNoManager extends AbstractManager<OrderNoEntity, Long, OrderNoDao> {
    /**
     * 获取订单号.
     * @param orderType 订单类型
     * @param hightCode 高位码
     * @param length 订单号长度
     */
    @Transactional
    public String createOrderNo(String orderType, String hightCode, int length) {
        String lowCode = DateUtils.toString(new Date(), "yyyyMMdd");
        OrderNoEntity orderNoEntity = new OrderNoEntity();
        orderNoEntity.setOrderType(orderType);
        orderNoEntity.setHightCode(hightCode);
        orderNoEntity.setLowCode(lowCode);

        save(orderNoEntity);

        long lowCodeL = Long.parseLong(orderNoEntity.getLowCode())+orderNoEntity.getId();

        String orderNo = lowCodeL+"";

        while (orderNo.length() < length) {
            orderNo = "0" + orderNo;
        }

        if (StringUtils.isNotBlank(hightCode)) {
            orderNoEntity.setOrderNo(hightCode+orderNo);
        } else {
            orderNoEntity.setOrderNo(orderNo);
        }

        save(orderNoEntity);

        return orderNoEntity.getOrderNo();
    }

    @Override
    protected OrderNoDao getDao() {
        return dao;
    }

    @Autowired
    private OrderNoDao dao;
}
