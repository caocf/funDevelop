package com.fundevelop.framework.manager.jpa.query.dynamic;

import com.fundevelop.commons.utils.BackgroundJobHelps;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import ognl.OgnlOps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 动态查询辅助类.
 * <p>描述:进行动态查询</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 15:42
 */
public class QueryUtils {
    /**
     * 根据查询计划参数获取查询计划.
     */
    @SuppressWarnings("unchecked")
    public static <ID extends Serializable, T extends BaseEntity<ID>> Class<T> getQueryPlan(Class<?> controllerClass, String planName) {
        if (StringUtils.isNotBlank(planName) && controllerClass != null) {
            QueryPlan plan = controllerClass.getAnnotation(QueryPlan.class);

            if (plan != null) {
                if (planName.trim().toLowerCase().equals(plan.name().trim().toLowerCase())) {
                    return (Class<T>) plan.returnType();
                }
            }

            QueryPlans plans = controllerClass.getAnnotation(QueryPlans.class);

            if (plans != null) {
                for (QueryPlan p : plans.value()) {
                    if (planName.trim().toLowerCase().equals(p.name().trim().toLowerCase())) {
                        return (Class<T>) p.returnType();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 执行分页查询.
     */
    public static <T extends BaseEntity<?>> Page<T> doQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType) {
        return doQuery(searchFilters, pageable, entityManager, returnType, false);
    }

    /**
     * 执行分页查询.
     */
    public static <T extends BaseEntity<?>> Page<T> doQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType, boolean distinct) {
        return doQuery(searchFilters, pageable, entityManager, returnType, false, false);
    }

    /**
     * 执行分页查询.
     */
    public static <T extends BaseEntity<?>> Page<T> doSyncQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType) {
        return doSyncQuery(searchFilters, pageable, entityManager, returnType, false);
    }

    /**
     * 执行分页查询.
     */
    public static <T extends BaseEntity<?>> Page<T> doSyncQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType, boolean distinct) {
        return doQuery(searchFilters, pageable, entityManager, returnType, true, false);
    }

    /**
     * 执行列表查询.
     */
    public static <T extends BaseEntity<?>> List<T> doListQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType) {
        return doListQuery(searchFilters, pageable, entityManager, returnType, false);
    }

    /**
     * 执行列表查询.
     */
    public static <T extends BaseEntity<?>> List<T> doListQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType, boolean distinct) {
        QueryBuilder queryBuilder = QueryBuilder.getQueryBuilder(entityManager, returnType);
        Query query = queryBuilder.createQuery(searchFilters, pageable, distinct);

        return query.getResultList();
    }

    /**
     * 执行列表查询.
     */
    public static <T extends BaseEntity<?>> List<T> doListQuery(List<SearchFilter> searchFilters, Sort sort, EntityManager entityManager, Class<T> returnType) {
        return doListQuery(searchFilters, sort, entityManager, returnType, false);
    }

    /**
     * 执行列表查询.
     */
    public static <T extends BaseEntity<?>> List<T> doListQuery(List<SearchFilter> searchFilters, Sort sort, EntityManager entityManager, Class<T> returnType, boolean distinct) {
        QueryBuilder queryBuilder = QueryBuilder.getQueryBuilder(entityManager, returnType);
        Query query = queryBuilder.createQuery(searchFilters, sort, distinct);

        return query.getResultList();
    }

    /**
     * 执行分页查询.
     */
    public static <T extends BaseEntity<?>> Page<T> doQuery(List<SearchFilter> searchFilters, Pageable pageable, EntityManager entityManager, Class<T> returnType, boolean sync, boolean distinct) {
        QueryBuilder queryBuilder = QueryBuilder.getQueryBuilder(entityManager, returnType);
        Query query = queryBuilder.createQuery(searchFilters, pageable, distinct);
        final Query countQuery = queryBuilder.getCountQuery();
        Long total = Long.valueOf(0);

        if (sync) {
            Future<Long> countFuture = BackgroundJobHelps.runJob(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    Long total = 0l;

                    try {
                        total = (Long)OgnlOps.convertValue(countQuery.getSingleResult(), Long.class);
                    } catch (Exception e) {
                        throw new DynamicQueryException("获取记录数出错", e);
                    }

                    return total;
                }
            });

            List<T> content = query.getResultList();

            try {
                total = countFuture.get();
            } catch (InterruptedException e) {
                countFuture.cancel(true);
                throw new DynamicQueryException("获取记录数出错", e);
            } catch (ExecutionException e) {
                throw new DynamicQueryException("获取记录数出错", e);
            }

            if (total > 0) {
                return new PageImpl<T>(content, pageable, total);
            }

            return new PageImpl<T>(Collections.<T> emptyList(), pageable, total);
        } else {
            try {
                total = (Long) OgnlOps.convertValue(countQuery.getSingleResult(), Long.class);
            } catch (Exception e) {
                throw new DynamicQueryException("获取记录数出错", e);
            }

            List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();

            return new PageImpl<T>(content, pageable, total);
        }
    }

    /** 查询计划参数名称. */
    public static String PLAN = "PLAN";
}
