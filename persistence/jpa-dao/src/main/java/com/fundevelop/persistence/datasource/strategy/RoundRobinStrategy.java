package com.fundevelop.persistence.datasource.strategy;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询策略.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 20:07
 */
public class RoundRobinStrategy extends AbstractStrategy {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    protected DataSource doSelect(List<DataSource> slaves, DataSource master) {
        int _index = index.getAndIncrement() % slaves.size();
        index.set(_index);
        return slaves.get(_index);
    }
}
