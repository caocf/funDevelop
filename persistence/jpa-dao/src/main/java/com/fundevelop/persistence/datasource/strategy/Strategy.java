package com.fundevelop.persistence.datasource.strategy;

import javax.sql.DataSource;

/**
 * 负载均衡.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 20:08
 */
public interface Strategy {
    DataSource select(java.util.List<DataSource> Slaves, DataSource master);
}
