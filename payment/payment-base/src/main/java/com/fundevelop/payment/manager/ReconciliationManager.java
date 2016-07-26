package com.fundevelop.payment.manager;

import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.payment.dao.ReconciliationDao;
import com.fundevelop.payment.entity.ReconciliationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 财务对账信息管理类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 1:34
 */
@Component
public class ReconciliationManager extends AbstractManager<ReconciliationEntity, Long, ReconciliationDao> {
    public ReconciliationEntity getByOrderNo(String orderNo) {
        return getDao().getByOrderNo(orderNo);
    }

    @Override
    protected ReconciliationDao getDao() {
        return dao;
    }

    @Autowired
    private ReconciliationDao dao;
}
