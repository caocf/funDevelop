package com.fundevelop.framework.erp.frame.web.Ace;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.framework.erp.frame.FrameDataModel;
import com.fundevelop.framework.erp.frame.web.Ace.model.JqGridFilter;
import com.fundevelop.framework.erp.frame.web.Ace.model.JqGridFilterRule;
import com.fundevelop.framework.erp.model.StatusResponse;
import com.fundevelop.framework.erp.web.BaseController;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JqGrid控件基础控制类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 18:38
 */
public abstract class JqGridController<T extends BaseEntity<ID>, ID extends Serializable, M extends AbstractManager> extends BaseController<T, ID, M> {
    /**
     * 处理POST请求（保存、删除）
     * @param entity 要操作的实体
     * @param oper 操作类别
     */
    @RequestMapping(value = "handle", method = RequestMethod.POST)
    @ResponseBody
    public Object handleGrid(@ModelAttribute("entity") T entity, @RequestParam("oper") String oper, HttpServletRequest request) {
        try {
            if (oper.equals("del")) {
                String jqId = getJqId(request);
                beforeDelete(jqId);
                getManager().batchDelete(jqId);
            } else {
                beforeSave(entity);
                getManager().save(entity);
            }
        } catch (Exception e) {
            long timeMillis = System.currentTimeMillis();
            logger.error("保存实体失败, tm={}, entity={}", timeMillis, entity, e);
            return new StatusResponse(500, "系统内部错误, 错误标示：" + timeMillis);
        }

        return true;
    }

    /**
     * 获取JqGrid控件上传的ID值.
     */
    protected String getJqId(HttpServletRequest request) {
        return request.getParameter(JQGRID_ID_PARAM_NAME);
    }

    /**
     * 获取实体ID.
     */
    @Override
    protected ID getId(HttpServletRequest request) {
        String jqId = getJqId(request);

        if (StringUtils.isNotBlank(jqId) && !JQGRID_ID_EMPTY.equals(jqId)) {
            try {
                return (ID)BeanUtils.convertValue(entityKeyClazz ,jqId);
            } catch (Exception e) {
                logger.error("对JqGrid ID进行数据类型转换时发生异常,jqId:{},id数据类型:{}", jqId, entityKeyClazz, e);
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    protected List<SearchFilter> buildSearchFilter(HttpServletRequest request) {
        return appendSearchParams(super.buildSearchFilter(request), request);
    }

    @Override
    protected Integer getPageSize(HttpServletRequest request) {
        String ps = StringUtils.trim(request.getParameter(JQGRID_PAGESIZE_PARAM_NAME));

        if (StringUtils.isNumeric(ps)) {
            return Integer.parseInt(ps, 10);
        }

        return null;
    }

    /**
     * 追加JqGrid查询参数.
     */
    private static List<SearchFilter> appendSearchParams(List<SearchFilter> searchFilters, HttpServletRequest request) {
        try {
            String filterStr = request.getParameter(JQGRID_FILTERS_PARAM_NAME);

            if (searchFilters != null) {
                searchFilters.addAll(parse(filterStr).values());
            } else {
                searchFilters = (List<SearchFilter>) parse(filterStr).values();
            }
        } catch (Exception ex) {
            throw new RuntimeException("解析JqGrid查询参数发生异常", ex);
        }

        return searchFilters;
    }

    /**
     * 解析JqGrid查询参数.
     * @param filterStr JqGrid查询参数Json字符串
     * @throws IOException
     */
    private static Map<String, SearchFilter> parse(String filterStr) throws IOException {
        Map<String, SearchFilter> filters = new HashMap<>();

        JqGridFilter jqGridFilters = getJqGridFilters(filterStr);
        if (jqGridFilters == null) {
            return filters;
        }

        List<JqGridFilterRule> rules = jqGridFilters.getRules();
        for (JqGridFilterRule rule : rules) {
            // 过滤掉空值
            String key = rule.getField();
            Object value = rule.getData();
            if (value == null || (value instanceof String) && StringUtils.isBlank((String) value)) {
                continue;
            }

            SearchFilter.Operator operator = SearchFilter.Operator.valueOf(rule.getOp().toUpperCase());

            // 创建searchFilter
            SearchFilter filter = new SearchFilter(key, operator, value);
            filters.put(key, filter);
        }

        return filters;
    }

    /**
     * 将JqGrid查询参数Json字符串转换为对象.
     * @param filters JqGrid查询参数Json字符串
     * @throws IOException
     * @author <a href="mailto:yangmujiang@xiaomashijia.com">Reamy(杨木江)</a>
     * @date 2015-06-24  15:58:09
     */
    private static JqGridFilter getJqGridFilters(String filters) throws IOException {
        if (StringUtils.isBlank(filters)) {
            return null;
        }

        return BeanUtils.toBean(filters, JqGridFilter.class);
    }

    /** ID为空的标示 */
    private static final String JQGRID_ID_EMPTY = "_empty";
    private static final String JQGRID_ID_PARAM_NAME = "jqId";
    private static final String JQGRID_PAGESIZE_PARAM_NAME = "rows";
    private static final String JQGRID_FILTERS_PARAM_NAME = "filters";
}
