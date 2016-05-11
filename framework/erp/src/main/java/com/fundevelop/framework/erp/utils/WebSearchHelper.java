package com.fundevelop.framework.erp.utils;

import com.fundevelop.framework.erp.exception.FatalException;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.framework.manager.jpa.query.SearchUtils;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * 网页查询参数处理辅助类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/6 9:50
 */
public class WebSearchHelper {
    /**
     * 从请求中获取分页参数.
     * @param returnDefaultIfNotPageParam 如果没有分页查询是否返回系统默认分页对象
     */
    public static Pageable buildPageRequest(HttpServletRequest request, boolean returnDefaultIfNotPageParam) {
        boolean pageQuery = false;

        if (!pageQuery && !returnDefaultIfNotPageParam) {
            return null;
        }

        return buildPageRequest(1, 10);
    }

    /**
     * 构建分页查询条件.
     * @param pageNumber 页号（从1开始）
     * @param pageSize 每页记录数
     * @param orderStrs 排序字段，格式为：字段名称,[(ASC,DESC)],[字段名称]...
     * @return 分页查询条件
     */
    public static PageRequest buildPageRequest(int pageNumber, int pageSize, String... orderStrs) {
        Sort sort = null;

        if (orderStrs != null) {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            for (int i=0; i < orderStrs.length; i++) {
                String order = orderStrs[i];

                if (StringUtils.isNotBlank(order)) {
                    Sort.Direction direction = null;

                    if (i+1 < orderStrs.length && ("ASC".equalsIgnoreCase(orderStrs[i+1]) || "DESC".equalsIgnoreCase(orderStrs[i+1]))) {
                        direction = Sort.Direction.fromString(orderStrs[i+1]);
                        i++;
                    } else {
                        direction = Sort.DEFAULT_DIRECTION;
                    }

                    orders.add(new Sort.Order(direction, order));
                }
            }

            if (!orders.isEmpty()) {
                sort = new Sort(orders);
            }

            orders = null;
        }

        return new PageRequest(pageNumber - 1, pageSize, sort);
    }

    /**
     * 从请求中获取查询参数.
     * @param otClass 查询时对应的数据Bean
     * @param request HttpServletRequest
     * @throws FatalException
     */
    public static <OID extends Serializable,OT extends BaseEntity<OID>> Specification<OT> buildSpecification(Class<OT> otClass, HttpServletRequest request) throws FatalException {
//        Map<String, SearchFilter> baseSearchFilterMap = SearchUtils.parse(getParametersStartingWith(request, searchPre));
//        return SearchHelper.buildSpecification(JqGridUtil.appendSearchParams(SearchUtils.parse(ServletUtils.getParametersStartingWith(request, SearchFilter.searchPre)), request.getParameter("filters")).values(),otClass);
        return null;
    }

    /**
     * 取得带有相同前缀的Request 参数.
     * @param request Request对象
     * @param prefix 参数前缀名称
     * @return 去除前缀的参数集合
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Validate.notNull(request, "Request must not be null");
        @SuppressWarnings("unchecked")
        Enumeration<String> paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<>();

        if (prefix == null) {
            prefix = "";
        }

        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }

        return params;
    }

    private WebSearchHelper(){}

    /** 查询属性前缀. */
    private final static String searchPre = "Q_";
}
