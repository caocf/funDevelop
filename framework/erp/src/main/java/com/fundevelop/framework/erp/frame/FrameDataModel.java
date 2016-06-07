package com.fundevelop.framework.erp.frame;

import com.fundevelop.framework.erp.frame.datamodel.helps.BeanFilter;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 数据模型展示渲染接口.
 * <p>描述:各个表示层框架数据输出模型必须实现该接口</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 15:13
 */
public interface FrameDataModel<T extends BaseEntity<ID>, ID extends Serializable> extends BeanFilter {
    /**
     * 获取数据.
     * @return 集合数据
     */
    Object getData();

    /**
     * 是否将data作为根节点.
     * @return true：将data作为根节点；false：不作为根节点
     */
    boolean isUseDataOfRoot();

    /**
     * 设置数据.
     * @param data 集合数据
     */
    void setData(List<T> data);

    /**
     * 设置数据.
     * @param page 分页数据
     */
    void setData(Page<T> page);

    /**
     * 设置数据.
     * @param data 要设置的数据
     */
    void setData(Object data);

    /**
     * 设置数据.
     * @param page 分页数据
     * @param data 要替换的集合数据
     */
    void setData(Page<T> page, List<T> data);

    /**
     * 获取响应数据.
     */
    Object getResponse();
}
