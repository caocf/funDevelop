package com.fundevelop.framework.manager.jpa.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 动态查询过滤器.
 * <p>描述:构造查询条件</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 0:10
 */
public class SearchFilter {
    /**
     * 定义可用的查询操作符.
     * { oper:'eq', text:'等于'},
     * { oper:'ne', text:'不等'},
     * { oper:'lt', text:'小于'},
     * { oper:'le', text:'小于等于'},
     * { oper:'gt', text:'大于'},
     * { oper:'ge', text:'大于等于'},
     * { oper:'in', text:'属于'},
     * { oper:'ni', text:'不属于'},
     * { oper:'cn', text:'包含'},
     * { oper:'nc', text:'不包含'},
     * { oper:'nu', text:'不存在'},
     * { oper:'nn', text:'存在'},
     * { oper:'NU', text:'is null'},
     * { oper:'NNU', text:'is not null'},
     * { oper:'BLANK', text:'为空'},
     * { oper:'NBLANK', text:'不为空'}
     */
    public enum Operator {
        EQ, NE, LT, LE, GT, GE, IN, NI, CN, NC, NU, NN, BLANK, NBLANK
    }

    /** 字段名称. */
    public String fieldName;
    /** 字段值. */
    public Object value;
    /** 比较符. */
    public Operator operator;

    /**
     * 构造函数.
     * @param fieldName 字段名称
     * @param operator 比较符
     * @param value 字段值
     */
    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fieldName", fieldName)
                .append("value", value)
                .append("operator", operator)
                .toString();
    }
}
