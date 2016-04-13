package com.fundevelop.persistence.entity.hibernate;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 使用字符串作为主键的实体超类.
 * <p>描述:定义实体使用id作为主键。主键生成策略为UUID，长度为32的varchar2类型</p>
 * <p>如果需要修改主键对应的字段名称可以在实体类上使用注解：<br/>
 * @AttributeOverride(name="id", column=@Column(name="user_id"))  //修改主键对应的数据库字段为user_id</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 14:59
 */
@MappedSuperclass //JPA 基类的标识
public class UUIDEntity implements BaseEntity<String>  {
    @Override
    @Id
    @Column(length = 32, nullable = false)
    @GeneratedValue(generator = "uuid")   //指定生成器名称
    @GenericGenerator(name = "uuid", strategy = "uuid")  //生成器名称，uuid生成类
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** 数据表主键. */
    protected String id;
}
