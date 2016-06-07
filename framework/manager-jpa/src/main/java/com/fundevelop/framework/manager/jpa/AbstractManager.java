package com.fundevelop.framework.manager.jpa;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.ClassUtils;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.framework.manager.jpa.query.SearchUtils;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import com.fundevelop.persistence.jpa.BaseDao;
import com.fundevelop.persistence.jpa.JdbcDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 抽象基础服务类.
 * <p>描述:所有服务类的超类，所有服务类均需要继承该类</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 16:09
 */
@Transactional(readOnly = true)
public abstract class AbstractManager<T extends BaseEntity<ID>, ID extends Serializable,Dao extends BaseDao<T,ID>> {
    public  AbstractManager() {
        entityClazz = ClassUtils.getSuperClassGenricType(getClass(), 0);
        entityKeyClazz = ClassUtils.getSuperClassGenricType(getClass(), 1);
    }

    /**
     * 根据实体ID获取实体.
     * @param id 实体ID
     * @return 实体ID对应的实体
     */
    public T getByID(ID id){
        Assert.notNull(id, "要获取的实体ID不能为空");

        return getDao().findOne(id);
    }

    /**
     * 查询.
     * @param spec 查询条件，参照{@link Specification}
     * @return 查询结果
     */
    public List<T> find(Specification<T> spec){
        Assert.notNull(spec, "查询条件不能为空");
        return getDao().findAll(spec);
    }

    /**
     * 分页查询.
     * @param spec  查询条件，参照{@link Specification}
     * @param pageable 分页条件，参照{@link Pageable}
     * @return 查询结果
     */
    public Page<T> find(Specification<T> spec, Pageable pageable){
        Assert.notNull(spec, "查询条件不能为空");
        Assert.notNull(pageable, "分页条件不能为空");
        return getDao().findAll(spec, pageable);
    }

    public Page<T> find(List<SearchFilter> filters, Pageable pageable) {
        return find(SearchUtils.buildSpecification(filters, (Class<T>)entityClazz), pageable);
    }

    /**
     * 排序查询.
     * @param spec 查询条件，参照{@link Specification}
     * @param sort 排序规则，参照{@link Sort}
     * @return 查询结果
     */
    public List<T> find(Specification<T> spec, Sort sort){
        Assert.notNull(spec, "查询条件不能为空");
        Assert.notNull(sort, "排序规则不能为空");
        return getDao().findAll(spec, sort);
    }

    public List<T> find(SearchFilter...filters) {
        List<SearchFilter> filtersList = Arrays.asList(filters);
        return find(filtersList);
    }

    public List<T> find(List<SearchFilter> filters) {
        return find(SearchUtils.buildSpecification(filters, (Class<T>)entityClazz));
    }

    public List<T> find(List<SearchFilter> filters, Sort sort) {
        return find(SearchUtils.buildSpecification(filters, (Class<T>)entityClazz), sort);
    }

    public T findOne(List<SearchFilter> filters) {
        return getDao().findOne(SearchUtils.buildSpecification(filters, (Class<T>)entityClazz));
    }

    public T findOne(SearchFilter...filters) {
        List<SearchFilter> filtersList = Arrays.asList(filters);
        return findOne(filtersList);
    }

    public T findUniqueBy(String fieldName, Object value) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter(fieldName, SearchFilter.Operator.EQ, value));
        return findOne(filters);
    }

    public List<T> findBy(String fieldName, Object value) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter(fieldName, SearchFilter.Operator.EQ, value));
        return find(filters);
    }

    /**
     * 获取实体记录数.
     * @param filters 查询条件，参照{@link Specification}
     * @return 符合条件的实体数
     */
    public long count(List<SearchFilter> filters){
        return count(SearchUtils.buildSpecification(filters, (Class<T>)entityClazz));
    }

    /**
     * 获取实体记录数.
     * @param filters 查询条件
     * @return 符合条件的实体数
     */
    public long count(SearchFilter...filters){
        List<SearchFilter> filtersList = Arrays.asList(filters);
        return count(filtersList);
    }

    /**
     * 获取实体记录数.
     * @param spec 查询条件，参照{@link Specification}
     * @return 符合条件的实体数
     */
    public long count(Specification<T> spec){
        Assert.notNull(spec, "查询条件不能为空");
        return getDao().count(spec);
    }

    /**
     * 批量删除.
     * @param ids 要删除的主键ID,多个使用","进行分割
     */
    @Transactional
    public void batchDelete(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] split = StringUtils.split(ids, ",");
            for (String s : split) {
                ID rid = null;
                try {
                    rid = (ID) BeanUtils.convertValue(entityKeyClazz, s);
                } catch (Exception e) {
                    throw new RuntimeException("动态转换ID类型失败!", e);
                }

                delete(rid);
            }
        }
    }

    /**
     * 根据实体ID删除对应实体对象.
     * @param id 要删除的实体ID
     */
    @Transactional
    public void delete(ID id){
        Assert.notNull(id, "要删除的实体ID不能为空");

        delete(getByID(id));
    }

    /**
     * 删除实体.
     * @param entity 要删除的实体
     */
    @Transactional
    public void delete(T entity) {
        Assert.notNull(entity, "要删除的实体不能为空");
        beforeDelete(entity);
        getDao().delete(entity);
        afterDelete(entity);
    }

    /**
     * 批量删除实体.
     * @param entities 要删除的实体集合
     */
    @Transactional
    public void delete(Iterable<T> entities){
        Assert.notNull(entities, "要删除的实体不能为空");

        getDao().delete(entities);
    }

    /**
     * 保存实体.
     * @param entity 要保存的实体
     * @return 保存后实体对象
     */
    @Transactional
    public T save(T entity){
        beforeSave(entity);
        getDao().save(entity);
        afterSave(entity);

        return entity;
    }

    /**
     * 批量保存实体.
     * @param entities 要保存的实体集合
     * @return 保存后实体对象集合
     */
    @Transactional
    public Iterable<T> save(Iterable<T> entities){
        return getDao().save(entities);
    }

    /**
     * 获取JdbcDao.
     */
    public JdbcDao getJdbcDao() {
        return jdbcDao;
    }

    /**
     * 获取Dao实例.
     */
    protected abstract Dao getDao();

    protected void beforeSave(T entity) {}

    protected void afterSave(T entity) {}

    protected void beforeDelete(T entity) {}

    protected void afterDelete(T entity) {}

    @Autowired
    private JdbcDao jdbcDao;

    // -- 私有属性 --//
    private Class<?> entityClazz;
    private Class<?> entityKeyClazz;

    protected Logger logger = LoggerFactory.getLogger(getClass());
}
