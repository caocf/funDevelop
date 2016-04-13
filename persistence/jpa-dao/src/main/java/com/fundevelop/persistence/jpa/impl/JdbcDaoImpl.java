package com.fundevelop.persistence.jpa.impl;

import com.fundevelop.persistence.jpa.JdbcDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * JDBC方式进行动态查询接口实现类.
 * <p>描述:使用EntityManager方式来实现动态查询</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 16:28
 */
@Component
@Transactional(readOnly=true)
public class JdbcDaoImpl implements JdbcDao {
    @Override
    @Transactional
    public int execute(String sql) {
        return em.createNativeQuery(sql).executeUpdate();
    }

    @Override
    @Transactional
    public int execute(String sql, Object[] objs) {
        Query query = em.createNativeQuery(sql);
        int position = 1;

        for (Object obj : objs) {
            query.setParameter(position++, obj);
        }

        return em.createNativeQuery(sql).executeUpdate();
    }

    @Override
    public List<?> query(String sql) {
        return doQuery(sql, null, null, -1, -1);
    }

    @Override
    public List<?> query(String sql, Object[] objs) {
        return doQuery(sql, null, null, -1, -1, objs);
    }

    @Override
    public List<?> query(String sql, int startPosition, int maxResult, Object[] objs) {
        return doQuery(sql,null,null,startPosition,maxResult,objs);
    }

    @Override
    public <T> List<T> query(String sql, Class<T> entityClass) {
        return doQuery(sql,entityClass,null,-1,-1);
    }

    @Override
    public <T> List<T> query(String sql, Class<T> entityClass, Object[] objs) {
        return doQuery(sql,entityClass,null,-1,-1,objs);
    }

    @Override
    public <T> List<T> query(String sql, Class<T> entityClass, int startPosition, int maxResult, Object[] objs) {
        return doQuery(sql,entityClass,null,startPosition,maxResult,objs);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    private <T> List<T> doQuery(String sql,Class<T> clazz,String resultSetMapping, int startPosition, int maxResult,Object...objs){
        Query query = null;
        if(clazz!=null){
            query = em.createNativeQuery(sql, clazz);
        }else if(resultSetMapping != null){
            query = em.createNativeQuery(sql, resultSetMapping);
        } else{
            query = em.createNativeQuery(sql);
        }

        if(startPosition>-1&&maxResult>0){
            query.setFirstResult(startPosition).setMaxResults(maxResult);
        }else if(maxResult>0){
            query.setMaxResults(maxResult);
        }

        int position = 1;

        for (Object obj : objs) {
            query.setParameter(position++, obj);
        }

        return query.getResultList();
    }

    @PersistenceContext
    private EntityManager em;
}
