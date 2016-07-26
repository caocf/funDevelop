package com.fundevelop.framework.erp.audit;

import org.springframework.transaction.annotation.Transactional;

/**
 * 日志记录服务类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/6 19:03
 */
public interface LoggerService {
    /**
     * 保存日志.
     */
    @Transactional
    void save (ErpOperationLog operationLog);
}
