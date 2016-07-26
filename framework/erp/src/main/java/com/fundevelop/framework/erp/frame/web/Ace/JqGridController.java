package com.fundevelop.framework.erp.frame.web.Ace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.framework.erp.frame.web.Ace.model.EditableCheck;
import com.fundevelop.framework.erp.frame.web.Ace.model.JqGridFilter;
import com.fundevelop.framework.erp.frame.web.Ace.model.JqGridFilterRule;
import com.fundevelop.framework.erp.model.StatusResponse;
import com.fundevelop.framework.erp.web.BaseController;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
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
            boolean isEditable = isEditable(request);
            if (isEditable) {
                String fieldName = request.getParameter("name");
                String value = request.getParameter("value");

                EditableCheck check = beforeSetProperty(entity, fieldName, value);

                if (check != null && check.isSucess()) {
                    BeanUtils.setProperty(entity, fieldName, value);
                } else if (check != null) {
                    return check;
                }
            }

            if (oper.equals("del")) {
                String jqId = getJqId(request);
                beforeDelete(jqId);
                getManager().batchDelete(jqId);
            } else {
                beforeSave(entity);
                getManager().save(entity);
            }

            if (isEditable) {
                return new EditableCheck(true);
            }
        } catch (Exception e) {
            long timeMillis = System.currentTimeMillis();
            try {
                logger.error("保存实体失败, tm={}, entity={}", timeMillis, BeanUtils.toJson(entity), e);
            } catch (JsonProcessingException e1) {
                logger.error("保存实体失败, tm={}", timeMillis, e);
            }
            return new StatusResponse(500, "系统内部错误, 错误标示：" + timeMillis);
        }

        return true;
    }

    /**
     * 设置属性值前调用.
     */
    protected EditableCheck beforeSetProperty(T entity, String property, String value) {
        return new EditableCheck(true);
    }

    /**
     * 获取Editable控件上传的ID值.
     */
    protected String getEditableId(HttpServletRequest request) {
        return request.getParameter(EDITABLE_ID_PARAM_NAME);
    }

    /**
     * 验证是否是使用Editable控件进行值修改.
     */
    private boolean isEditable(HttpServletRequest request) {
        return StringUtils.isNotBlank(getEditableId(request));
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
                return (ID)BeanUtils.convertValue(entityKeyClazz, jqId);
            } catch (Exception e) {
                logger.error("对JqGrid ID进行数据类型转换时发生异常,jqId:{},id数据类型:{}", jqId, entityKeyClazz.getName(), e);
                throw new RuntimeException(e);
            }
        }

        String pk = getEditableId(request);

        if (StringUtils.isNotBlank(pk)) {
            try {
                return (ID)BeanUtils.convertValue(entityKeyClazz, pk);
            } catch (Exception e) {
                logger.error("Editable ID进行数据类型转换时发生异常,pk:{},id数据类型:{}", pk, entityKeyClazz.getName(), e);
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
    protected Integer getPageNo(HttpServletRequest request) {
        String po = StringUtils.trim(request.getParameter(JQGRID_PAGENO_PARAM_NAME));

        if (StringUtils.isNumeric(po)) {
            return Integer.parseInt(po, 10);
        }

        return null;
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
    private static final String JQGRID_PAGENO_PARAM_NAME = "page";
    private static final String JQGRID_PAGESIZE_PARAM_NAME = "rows";
    private static final String JQGRID_FILTERS_PARAM_NAME = "filters";

    private static final String EDITABLE_ID_PARAM_NAME = "pk";
}
