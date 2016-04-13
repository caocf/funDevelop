package com.fundevelop.persistence.jpa;

import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

/**
 * 基础DAO.
 * <p>描述:所有DAO均应继承该接口</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 15:30
 */
public interface BaseDao<T extends BaseEntity<ID>,ID extends Serializable> extends JpaRepository<T , ID>, JpaSpecificationExecutor<T> {
}
