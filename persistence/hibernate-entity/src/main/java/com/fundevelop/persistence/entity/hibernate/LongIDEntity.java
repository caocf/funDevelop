package com.fundevelop.persistence.entity.hibernate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 使用Long作为主键的实体超类.
 * <p>描述:定义实体使用id作为主键。主键生成策略为IDENTITY，自增长Long类型</p>
 * <p>如果需要修改主键对应的字段名称可以在实体类上使用注解：<br/>
 * @AttributeOverride(name="id", column=@Column(name="user_id"))  //修改主键对应的数据库字段为user_id</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 14:47
 */
@MappedSuperclass //JPA 基类的标识
public class LongIDEntity implements BaseEntity<Long> {
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /** 数据表主键. */
    protected Long id;
}
