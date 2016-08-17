package com.fundevelop.framework.manager.jpa.query;

import com.fundevelop.commons.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 查询规格实现类.
 * <p>描述:负责对查询条件进行详细说明</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 0:38
 */
public class SearchSpecification<T> implements Specification<T> {
    /**
     * 构造函数.
     * @param filters 查询条件过滤器
     * @param modelClass 查询对应的实体类
     */
    public SearchSpecification(final Collection<SearchFilter> filters, final Class<T> modelClass) {
        this(filters, null, modelClass);
    }

    /**
     * 构造函数.
     * @param filters 查询条件过滤器
     * @param orFilters Or查询条件过滤器
     * @param modelClass 查询对应的实体类
     */
    public SearchSpecification(final Collection<SearchFilter> filters, final Collection<SearchFilter> orFilters, final Class<T> modelClass) {
        this.filters = filters;
        this.modelClass = modelClass;
        this.orFilters = orFilters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate firstPredicate = null;
        Predicate secondPredicate = null;

        if (!CollectionUtils.isEmpty(filters)) {
            List<Predicate> predicates = buildPredicateByFilter(filters, root, builder);

            // 将所有条件用 and 联合起来
            if (!predicates.isEmpty()) {
                firstPredicate = builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }

        if (!CollectionUtils.isEmpty(orFilters)) {
            List<Predicate> predicates = buildPredicateByFilter(orFilters, root, builder);

            // 将所有条件用 and 联合起来
            if (!predicates.isEmpty()) {
                secondPredicate = builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }

        if (firstPredicate != null && secondPredicate != null) {
            return builder.or(firstPredicate, secondPredicate);
        } else if (firstPredicate != null) {
            return firstPredicate;
        } else if (secondPredicate != null) {
            return secondPredicate;
        }

        return builder.conjunction();
    }

    /**
     * 根据查询过滤参数构造查询条件.
     * @param filters 查询过滤参数
     */
    private List<Predicate> buildPredicateByFilter(final Collection<SearchFilter> filters, Root<?> root, CriteriaBuilder builder) {
        if (filters != null && !filters.isEmpty()) {
            List<Predicate> predicates = new ArrayList<>(filters.size());

            for (SearchFilter filter : filters) {
                predicates.add(processFilter(filter, root, builder));
            }

            return predicates;
        }

        return null;
    }

    private Predicate processFilter(SearchFilter filter, Root<?> root, CriteriaBuilder builder) {
        Predicate predicate = buildFilter(filter, root, builder);

        if (filter.hasOrFilter()) {
            List<Predicate> predicates = new ArrayList<>(filter.getOrFilters().size()+1);
            predicates.add(predicate);

            for (SearchFilter orFilter : filter.getOrFilters()) {
                predicates.add(buildFilter(orFilter, root, builder));
            }

            return builder.or(predicates.toArray(new Predicate[predicates.size()]));
        } else {
            return predicate;
        }
    }

    private Predicate buildFilter(SearchFilter filter, Root<?> root, CriteriaBuilder builder) {
        String[] names = StringUtils.split(filter.fieldName, ".");
        Path expression = root.get(names[0]);
        for (int i = 1; i < names.length; i++) {
            expression = expression.get(names[i]);
        }

        switch (filter.operator) {
            case EQ:
                return builder.equal(expression, BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case NE:
                return builder.notEqual(expression, BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case LT:
                return builder.lessThan(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case LE:
                return builder.lessThanOrEqualTo(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case GT:
                return builder.greaterThan(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case GE:
                return builder.greaterThanOrEqualTo(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value));
            case IN:
                CriteriaBuilder.In in = builder.in(expression);

                if (filter.value.getClass().isArray()) {
                    for (Object value : (Object[])filter.value) {
                        in.value((Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, value));
                    }
                } else if (filter.value instanceof  Collection) {
                    for (Object value : (Collection)filter.value) {
                        in.value((Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, value));
                    }
                } else {
                    in.value(filter.value);
                }

                return in;
            case NI:
                CriteriaBuilder.In notin = builder.in(expression);

                if (filter.value.getClass().isArray()) {
                    for (Object value : (Object[])filter.value) {
                        notin.value((Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, value));
                    }
                } else if (filter.value instanceof  Collection) {
                    for (Object value : (Collection)filter.value) {
                        notin.value((Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, value));
                    }
                } else {
                    notin.value(filter.value);
                }

                return builder.not(notin);
            case CN:
                return builder.like(expression, "%" + filter.value + "%");
            case NC:
                return builder.notLike(expression, "%" + filter.value + "%");
            case LL:
                return builder.like(expression, filter.value + "%");
            case RL:
                return builder.like(expression, "%" + filter.value);
            case NU:
                return builder.isNull(expression);
            case NN:
                return builder.isNotNull(expression);
            case BLANK:
                return builder.equal(expression, "");
            case NBLANK:
                return builder.notEqual(expression, "");
            default:
                throw new RuntimeException("未知的查询比较符");
        }
    }

    /** 查询条件过滤器. */
    final Collection<SearchFilter> filters;
    /** 查询条件过滤器. */
    final Collection<SearchFilter> orFilters;
    /** 查询对应的实体类. */
    final Class<T> modelClass;
}
