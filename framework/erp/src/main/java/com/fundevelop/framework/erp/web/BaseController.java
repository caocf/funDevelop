package com.fundevelop.framework.erp.web;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.ReflectionUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.erp.frame.FrameDataModel;
import com.fundevelop.framework.erp.frame.FrameDataModuleHanding;
import com.fundevelop.framework.erp.utils.WebSearchHelper;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.framework.manager.jpa.query.SearchUtils;
import com.fundevelop.framework.manager.jpa.query.dynamic.QueryUtils;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 基础控制器，提供基本的CRUD功能.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/5 21:27
 */
public abstract class BaseController<T extends BaseEntity<ID>, ID extends Serializable, M extends AbstractManager> {
    /**
     * 初始化实体.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @ModelAttribute
    public void setModel(Model model, HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        ID id = getId(request);
        T entity = null;

        if (id != null) {
            entity = (T)getManager().getByID(id);
        } else {
            entity = entityClazz.newInstance();
        }

        model.addAttribute("entity", entity);
    }

    /**
     * 删除实体.
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean delete(String ids) {
        try {
            beforeDelete(ids);
            getManager().batchDelete(ids);
        } catch (Exception ex) {
            logger.error("删除记录失败", ex);
            return false;
        }

        return true;
    }

    /**
     * 保存实体.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public T save(@ModelAttribute("entity") T entity) {
        beforeSave(entity);
        getManager().save(entity);

        return entity;
    }

    /**
     * 保存之前回调.
     */
    protected void beforeSave(T entity) {}

    /**
     * 删除前回调.
     */
    protected void beforeDelete(String ids) {}

    /**
     * 获取实体详情.
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public T getDetail(@PathVariable("id") ID id) {
        T entity = (T)getManager().getByID(id);

        if (entity != null) {
            return entity;
        }

        try {
            return entityClazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询列表.
     */
    @RequestMapping(value = "search")
    @ResponseBody
    public Object searchData(HttpServletRequest request) {
        FrameDataModel dataModel = getDataModel(request, getId(request));

        Pageable pageRequest = buildPageRequest(request);

        if (pageRequest == null) {
            dataModel.setData(getManager().find(buildSpecification(request)));
        } else {
            if (isSortOnly(request)) {
                dataModel.setData(getManager().find(buildSearchFilter(request), pageRequest.getSort()));
            } else {
                dataModel.setData(getManager().find(buildSpecification(request), pageRequest));
            }
        }

        return dataModel.getResponse();
    }

    /**
     * 使用查询计划进行动态查询.
     */
    @RequestMapping(value = "dsearch")
    @ResponseBody
    public Object dynamicSearch(HttpServletRequest request) {
        Class queryBean = QueryUtils.getQueryPlan(this.getClass(), request.getParameter(WebSearchHelper.searchPre+QueryUtils.PLAN));
        if (queryBean != null) {
            FrameDataModel dataModel = getDataModel(queryBean, null, request);
            boolean distinctQuery = com.fundevelop.commons.utils.StringUtils.isBooleanTrue(request.getParameter(WebSearchHelper.searchPre+QueryUtils.DISTINCT_QUERY));

            Pageable pageRequest = buildPageRequest(request, false);

            if (pageRequest == null) {
                dataModel.setData(QueryUtils.doListQuery(buildSearchFilter(request), pageRequest, entityManager, queryBean, distinctQuery));
            } else {
                if (isSortOnly(request)) {
                    dataModel.setData(QueryUtils.doListQuery(buildSearchFilter(request), pageRequest.getSort(), entityManager, queryBean, distinctQuery));
                } else {
                    dataModel.setData((Page) QueryUtils.doQuery(buildSearchFilter(request), pageRequest, entityManager, queryBean, distinctQuery));
                }
            }

            return dataModel.getResponse();
        } else {
            throw new RuntimeException("找不到指定的查询计划");
        }
    }

    public BaseController() {
        entityClazz = ReflectionUtils.getSuperClassGenricType(getClass(), 0);
        entityKeyClazz = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
    }

    /**
     * 获取对应的数据模型.
     */
    protected FrameDataModel<T, ID> getDataModel(HttpServletRequest request, ID id) {
        return getDataModel(entityClazz, id, request);
    }

    /**
     * 获取对应的数据模型.
     * @param otClass 模型使用的实体类
     * @param oid 模型使用的实体类ID类型
     */
    protected <OID extends Serializable, OT extends BaseEntity<OID>> FrameDataModel<OT, OID> getDataModel(Class<OT> otClass, OID oid, HttpServletRequest request) {
        return FrameDataModuleHanding.getDataModel(getFrameName(request), getModelName(request), otClass, oid, request);
    }

    /**
     * 获取要使用的前端表示层框架名称.
     * @return 前端表示层框架名称
     */
    protected String getFrameName(HttpServletRequest request) {
        String frameName = request.getParameter(FrameDataModuleHanding.FRAME_PARAM);

        if (!StringUtils.isEmpty(frameName)) {
            return frameName;
        }

        frameName = (String)request.getAttribute(FrameDataModuleHanding.FRAME_PARAM);

        if (!StringUtils.isEmpty(frameName)) {
            return frameName;
        }

        frameName = (String)request.getSession().getAttribute(FrameDataModuleHanding.FRAME_PARAM);

        if (!StringUtils.isEmpty(frameName)) {
            return frameName;
        }

        return PropertyUtil.get("fun.frontFrame.name");
    }

    /**
     * 获取要使用的数据模型名称.
     */
    protected String getModelName(HttpServletRequest request) {
        String modelName = request.getParameter(FrameDataModuleHanding.MODEL_PARAM);

        if (!StringUtils.isEmpty(modelName)) {
            return modelName;
        }

        modelName = (String)request.getAttribute(FrameDataModuleHanding.MODEL_PARAM);

        if (!StringUtils.isEmpty(modelName)) {
            return modelName;
        }

        return PropertyUtil.get("fun.frontModule.name");
    }

    /**
     * 构建查询条件.
     */
    protected Specification<T> buildSpecification(HttpServletRequest request) {
        return buildSpecification(entityClazz, request);
    }

    /**
     * 构建查询条件.
     * @param otClass 绑定的实体类
     * @return 查询条件
     */
    protected <OID extends Serializable,OT extends BaseEntity<OID>> Specification<OT> buildSpecification(Class<OT> otClass, HttpServletRequest request) {
        return SearchUtils.buildSpecification(buildSearchFilter(request), otClass);
    }

    /**
     * 构建查询条件.
     */
    protected List<SearchFilter> buildSearchFilter(HttpServletRequest request) {
        Collection<SearchFilter> filters = SearchUtils.parse(WebSearchHelper.getParametersStartingWith(request, WebSearchHelper.searchPre)).values();
        List<SearchFilter> searchFilters = new ArrayList<>(filters.size());
        searchFilters.addAll(filters);

        return searchFilters;
    }

    /**
     * 构建分页条件.
     */
    protected Pageable buildPageRequest(HttpServletRequest request) {
        return buildPageRequest(request, false);
    }

    /**
     * 构建分页条件.
     * @param returnDefaultIfNotPageParam 如果没有分页查询是否返回系统默认分页对象
     */
    protected Pageable buildPageRequest(HttpServletRequest request, boolean returnDefaultIfNotPageParam) {
        return WebSearchHelper.buildPageRequest(getPageNo(request), getPageSize(request), getOrder(request), returnDefaultIfNotPageParam);
    }

    /**
     * 验证是否只是进行排序处理.
     * @return true：只排序；false：分页并排序
     */
    public boolean isSortOnly(HttpServletRequest request) {
        if (getPageNo(request) != null || getPageSize(request) != null) {
            return false;
        }

        return true;
    }

    /**
     * 获取分页查询时每页记录数.
     */
    protected Integer getPageSize(HttpServletRequest request) {
        String ps = StringUtils.trim(request.getParameter(PropertyUtil.get("fun.search.paramname.pagesize", WebSearchHelper.PAGESIZE_PARAMNAME)));

        if (StringUtils.isNumeric(ps)) {
            return Integer.parseInt(ps, 10);
        }

        return null;
    }

    /**
     * 获取分页查询时查询页号.
     */
    protected Integer getPageNo(HttpServletRequest request) {
        String po = StringUtils.trim(request.getParameter(PropertyUtil.get("fun.search.paramname.pageno", WebSearchHelper.PAGENO_PARAMNAME)));

        if (StringUtils.isNumeric(po)) {
            return Integer.parseInt(po, 10);
        }

        return null;
    }

    /**
     * 获取排序字符串.
     */
    protected String getOrder(HttpServletRequest request) {
        String orderStr = StringUtils.trim(request.getParameter(PropertyUtil.get("fun.search.paramname.order", WebSearchHelper.ORDER_PARAMNAME)));
        return StringUtils.defaultString(FrameDataModuleHanding.getOrder(getFrameName(request), getModelName(request), orderStr, request), orderStr);
    }

    /**
     * 获取实体ID.
     */
    protected ID getId(HttpServletRequest request) {
        String id = request.getParameter("id");

        if (StringUtils.isNotBlank(id)) {
            try {
                return (ID) BeanUtils.convertValue(entityKeyClazz ,id);
            } catch (Exception e) {
                logger.error("对id进行数据类型转换时发生异常,id:{},id数据类型:{}", id, entityKeyClazz.getName(), e);
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    /**
     * 获取管理类实例.
     */
    protected abstract M getManager();

    @PersistenceContext
    protected EntityManager entityManager;

    // -- 私有属性 --//
    protected Class<T> entityClazz;
    protected Class<ID> entityKeyClazz;

    protected Logger logger = LoggerFactory.getLogger(getClass());
}
