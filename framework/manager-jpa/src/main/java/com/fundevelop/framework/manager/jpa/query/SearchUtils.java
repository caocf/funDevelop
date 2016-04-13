package com.fundevelop.framework.manager.jpa.query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 0:27
 */
public class SearchUtils {
    /**
     * 解析查询参数并构造查询过滤器.
     * @param searchParams searchParams中key的格式为OPERATOR_FIELDNAME.
     * @return 查询过滤器
     */
    public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = new HashMap<>();

        for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
            // 过滤掉空值
            String key = entry.getKey();
            Object value = entry.getValue();

            // 拆分operator与filedAttribute
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2) {
                throw new IllegalArgumentException(key + " 不是有效的查询过滤属性名");
            }

            String filedName = names[1];
            SearchFilter.Operator operator = SearchFilter.Operator.valueOf(names[0]);

            if (value instanceof String && StringUtils.isBlank((String) value)) {
                if (SearchFilter.Operator.NU != operator && SearchFilter.Operator.BLANK != operator && SearchFilter.Operator.NN != operator  && SearchFilter.Operator.NBLANK != operator) {
                    continue;
                }
            }

            // 创建searchFilter
            SearchFilter filter = new SearchFilter(filedName, operator, value);
            filters.put(key, filter);
        }

        return filters;
    }

    /**
     * 构建查询条件.
     * @param filters 查询条件集合
     * @return 查询条件
     */
    public static <T> Specification<T> buildSpecification(final Collection<SearchFilter> filters, final Class<T> modelClass) {
        return new SearchSpecification<T>(filters, modelClass);
    }

    /**
     * 构建查询条件.
     * @param filters 查询条件集合
     * @param orFilters Or查询条件过滤器
     * @return 查询条件
     */
    public static <T> Specification<T> buildSpecification(final Collection<SearchFilter> filters, final Collection<SearchFilter> orFilters, final Class<T> modelClass) {
        return new SearchSpecification<T>(filters, orFilters, modelClass);
    }
}
