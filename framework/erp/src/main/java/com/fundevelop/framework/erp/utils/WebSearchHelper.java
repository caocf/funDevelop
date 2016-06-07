package com.fundevelop.framework.erp.utils;

import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.ServletRequest;
import java.util.*;

/**
 * 网页查询参数处理辅助类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/6 9:50
 */
public class WebSearchHelper {
    /**
     * 构建分页查询条件.
     * @param pageNumber 页号（从1开始）
     * @param pageSize 每页记录数
     * @param orderStrs 排序字段，格式为：字段名称,[(ASC,DESC)],[字段名称]...
     * @return 分页查询条件
     */
    public static Pageable buildPageRequest(Integer pageNumber, Integer pageSize, String orderStrs, boolean returnDefaultIfNotPageParam) {
        Sort sort = getSort(orderStrs);

        if (pageNumber == null && pageSize == null && sort == null && !returnDefaultIfNotPageParam) {
            return null;
        }

        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = Integer.parseInt(PropertyUtil.get("fun.search.default.pagesize", "10"), 10);
        }

        if (sort == null) {
            return new PageRequest(pageNumber- 1, pageSize);
        }

        return new PageRequest(pageNumber - 1, pageSize, sort);
    }

    /**
     * 获取排序字段.
     * @param order 排序字段，格式为：字段名称,[(ASC,DESC)],[字段名称]...
     */
    public static Sort getSort(String order) {
        Sort sort = null;

        if (StringUtils.isNotBlank(order)) {
            String[] orderStrs = order.split(",");
            List<Sort.Order> orders = new ArrayList<Sort.Order>(orderStrs.length);

            for (int i=0; i < orderStrs.length; i++) {
                String o = orderStrs[i];

                if (StringUtils.isNotBlank(o)) {
                    Sort.Direction direction = null;

                    if (i+1 < orderStrs.length && ("ASC".equalsIgnoreCase(orderStrs[i+1]) || "DESC".equalsIgnoreCase(orderStrs[i+1]))) {
                        direction = Sort.Direction.fromString(orderStrs[i+1]);
                        i++;
                    } else {
                        direction = Sort.DEFAULT_DIRECTION;
                    }

                    orders.add(new Sort.Order(direction, o));
                }
            }

            if (!orders.isEmpty()) {
                sort = new Sort(orders);
            }
        }

        return sort;
    }

    /**
     * 取得带有相同前缀的Request 参数.
     * @param request Request对象
     * @param prefix 参数前缀名称
     * @return 去除前缀的参数集合
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        return getParametersStartingWith(request, prefix, false);
    }

    /**
     * 取得带有相同前缀的Request 参数.
     * @param request Request对象
     * @param prefix 参数前缀名称
     * @param checkAttribute 是否检测Attribute中的参数
     * @return 去除前缀的参数集合
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix, boolean checkAttribute) {
        Validate.notNull(request, "Request must not be null");

        Map<String, Object> params = getParameters(request, prefix, false);

        if (checkAttribute) {
            params.putAll(getParameters(request, prefix, true));
        }

        return params;
    }

    private static Map<String, Object> getParameters(ServletRequest request, String prefix, boolean fromAttribute) {
        Enumeration<String> paramNames = fromAttribute?request.getAttributeNames():request.getParameterNames();
        Map<String, Object> params = new TreeMap<>();

        if (prefix == null) {
            prefix = "";
        }

        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());

                if (fromAttribute) {
                    params.put(unprefixed, request.getAttribute(paramName));
                } else {
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
        }

        return params;
    }

    private WebSearchHelper(){}

    /** 默认分页每页记录数参数名称. */
    public static final String PAGESIZE_PARAMNAME = "P_rows";
    /** 默认分页页号参数名称. */
    public static final String PAGENO_PARAMNAME = "P_page";
    /** 默认排序参数名. */
    public static final String ORDER_PARAMNAME = "P_sord";

    /** 查询属性前缀. */
    public final static String searchPre = "Q_";
}
