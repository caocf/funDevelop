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
            List<Predicate> predicates = new ArrayList<>();

            for (SearchFilter filter : filters) {
                String[] names = StringUtils.split(filter.fieldName, ".");
                Path expression = root.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    expression = expression.get(names[i]);
                }

                switch (filter.operator) {
                    case EQ:
                        predicates.add(builder.equal(expression, BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
                    case NE:
                        predicates.add(builder.notEqual(expression, BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
                    case LT:
                        predicates.add(builder.lessThan(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
                    case LE:
                        predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
                    case GT:
                        predicates.add(builder.greaterThan(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
                    case GE:
                        predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) BeanUtils.convertValue(modelClass, filter.fieldName, filter.value)));
                        break;
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

                        predicates.add(in);
                        break;
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

                        predicates.add(builder.not(notin));
                        break;
                    case CN:
                        predicates.add(builder.like(expression, "%" + filter.value + "%"));
                        break;
                    case NC:
                        predicates.add(builder.notLike(expression, "%" + filter.value + "%"));
                        break;
                    case NU:
                        predicates.add(builder.isNull(expression));
                        break;
                    case NN:
                        predicates.add(builder.isNotNull(expression));
                        break;
                    case BLANK:
                        predicates.add(builder.equal(expression, ""));
                        break;
                    case NBLANK:
                        predicates.add(builder.notEqual(expression, ""));
                        break;
                    default:
                        break;
                }
            }

            return predicates;
        }

        return null;
    }

    /** 查询条件过滤器. */
    final Collection<SearchFilter> filters;
    /** 查询条件过滤器. */
    final Collection<SearchFilter> orFilters;
    /** 查询对应的实体类. */
    final Class<T> modelClass;
}
