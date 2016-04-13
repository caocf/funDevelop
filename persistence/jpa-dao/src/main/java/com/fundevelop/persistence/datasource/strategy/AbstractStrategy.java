package com.fundevelop.persistence.datasource.strategy;

import javax.sql.DataSource;

/**
 * 主从选择负载均衡.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 20:09
 */
public abstract class AbstractStrategy implements Strategy {
    public DataSource select(java.util.List<DataSource> slaves, DataSource master) {
        if (slaves == null || slaves.isEmpty())
            return master;
        if (slaves.size() == 1)
            return slaves.get(0);
        return doSelect(slaves, master);
    }

    protected abstract DataSource doSelect(java.util.List<DataSource> slaves, DataSource master);
}
