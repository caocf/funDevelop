package com.fundevelop.persistence.entity.hibernate;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 使用自定义方式生成主键的实体超类.
 * <p>描述:定义实体使用id作为主键。主键生成策略由实体类在函数getId上使用的注解决定</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 15:01
 */
@MappedSuperclass //JPA 基类的标识
public abstract class AssignedIDEntity<ID extends Serializable> implements BaseEntity<ID> {
    /** 数据表主键. */
    protected ID id;
}
