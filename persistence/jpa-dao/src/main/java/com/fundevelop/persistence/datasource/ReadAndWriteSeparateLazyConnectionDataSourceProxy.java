package com.fundevelop.persistence.datasource;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.persistence.datasource.strategy.RoundRobinStrategy;
import com.fundevelop.persistence.datasource.strategy.Strategy;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 动态数据源.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 20:04
 */
public class ReadAndWriteSeparateLazyConnectionDataSourceProxy extends LazyConnectionDataSourceProxy {
    private static Logger LOGGER = LoggerFactory.getLogger(ReadAndWriteSeparateLazyConnectionDataSourceProxy.class);

    //主数据源(  即写入数据源 )
    private DataSource master;

    //从数据源 ( 即读数据源 Strategy )
    private java.util.List<DataSource> slaves;

    //获取数据源的策略
    private Class<? extends Strategy> strategyClass = RoundRobinStrategy.class;

    //策略
    private Strategy strategy;

    public ReadAndWriteSeparateLazyConnectionDataSourceProxy() {
    }

    @Override
    public DataSource getTargetDataSource() {
        if (!isReadWriteSwitch()) {
            return getMaster();
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return getMaster();
        }
        return getSlave();
    }

    /**
     * 是否启用读写分离
     */
    protected boolean isReadWriteSwitch() {
        return BooleanUtils.toBoolean(PropertyUtil.get("db.datasource.rw.switch", "true"));
    }

    public DataSource getSlave() {
        if (null == strategy) {
            strategy = BeanUtils.instantiate(strategyClass);
        }
        return strategy.select(slaves, master);
    }


    public DataSource getMaster() {
        if (null == master)
            return super.getTargetDataSource();
        return master;
    }

    public void setMaster(DataSource master) {
        this.master = master;
    }

    public java.util.List<DataSource> getSlaves() {
        return slaves;
    }

    public void setSlaves(java.util.List<DataSource> slaves) {
        this.slaves = slaves;
    }

    public void setSlave(DataSource slave) {
        this.slaves = Arrays.asList(slave);
    }

    public Class<? extends Strategy> getStrategyClass() {
        return strategyClass;
    }

    public void setStrategyClass(Class<? extends Strategy> strategyClass) {
        this.strategyClass = strategyClass;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
