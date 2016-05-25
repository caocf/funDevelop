package com.fundevelop.framework.erp.web;

import com.fundevelop.commons.utils.ReflectionUtils;
import com.fundevelop.framework.erp.exception.FatalException;
import com.fundevelop.framework.erp.utils.WebSearchHelper;
import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 基础控制器，提供基本的CRUD功能.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/5 21:27
 */
public abstract class BaseController<T extends BaseEntity<ID>, ID extends Serializable, M extends AbstractManager> {
    public BaseController() {
        entityClazz = ReflectionUtils.getSuperClassGenricType(getClass(), 0);
        entityKeyClazz = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
    }

    /**
     * 构建分页条件.
     */
    public Pageable buildPageRequest(HttpServletRequest request) {
        return buildPageRequest(request, false);
    }

    /**
     * 构建分页条件.
     * @param returnDefaultIfNotPageParam 如果没有分页查询是否返回系统默认分页对象
     */
    public Pageable buildPageRequest(HttpServletRequest request, boolean returnDefaultIfNotPageParam) {
        return WebSearchHelper.buildPageRequest(request, returnDefaultIfNotPageParam);
    }

    /**
     * 构建查询条件.
     * @throws FatalException
     */
    public Specification<T> buildSpecification(HttpServletRequest request) throws FatalException {
        return buildSpecification(entityClazz, request);
    }

    /**
     * 构建查询条件.
     * @param otClass 绑定的实体类
     * @return 查询条件
     * @throws FatalException
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> Specification<OT> buildSpecification(Class<OT> otClass, HttpServletRequest request) throws FatalException {
        return WebSearchHelper.buildSpecification(otClass, request);
    }

    /**
     * 获取管理类实例.
     */
    protected abstract M getManager();

    // -- 私有属性 --//
    private Class<T> entityClazz;
    private Class<ID> entityKeyClazz;

    protected Logger logger = LoggerFactory.getLogger(getClass());
}
