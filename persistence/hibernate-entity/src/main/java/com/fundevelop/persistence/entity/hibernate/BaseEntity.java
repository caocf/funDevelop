package com.fundevelop.persistence.entity.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 所有实体类的超类.
 * <p>描述:定义实体主键。要求每个实体表中必须包含id字段作为主键</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 14:07
 */

@JsonIgnoreProperties (value = { "hibernateLazyInitializer"})
public interface BaseEntity<ID extends Serializable> extends Serializable {
    /**
     * 获取ID.
     */
    public ID getId();

    /**
     * 设置ID.
     */
    public void setId(ID id);
}
