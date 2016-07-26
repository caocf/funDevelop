package com.fundevelop.payment.dao;

import com.fundevelop.payment.entity.ReconciliationEntity;
import com.fundevelop.persistence.jpa.BaseDao;

/**
 * 财务对账信息Dao.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 1:33
 */
public interface ReconciliationDao extends BaseDao<ReconciliationEntity, Long> {
    ReconciliationEntity getByOrderNo(String orderNo);
}
