package com.fundevelop.persistence.jpa;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * JDBC方式进行动态查询接口定义类.
 * <p>描述:定义可用的JDBC查询方法</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 16:24
 */
public interface JdbcDao {
    /**
     * 执行sql.
     */
    @Transactional
    public int execute(String sql);

    /**
     * 执行sql.
     */
    @Transactional
    public int execute(String sql, Object[] objs);

    /**
     * 查询.
     */
    public List<?> query(String sql);

    /**
     * 查询.
     */
    public List<?> query(String sql, Object[] objs);

    /**
     * 查询.
     */
    public List<?> query(String sql, int startPosition, int maxResult, Object[] objs);

    /**
     * 查询.
     */
    public <T> List<T> query(String sql, Class<T> entityClass);

    /**
     * 查询.
     */
    public <T> List<T> query(String sql, Class<T> entityClass, Object[] objs);

    /**
     * 查询.
     */
    public <T> List<T> query(String sql, Class<T> entityClass, int startPosition, int maxResult, Object[] objs);

    /**
     * 获取实体管理对象.
     */
    public EntityManager getEntityManager();
}
