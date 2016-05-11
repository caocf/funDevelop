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
     * { oper:'EQ', text:'='},
     * { oper:'NE', text:'!='},
     * { oper:'LT', text:'<'},
     * { oper:'LE', text:'<='},
     * { oper:'GT', text:'>'},
     * { oper:'GE', text:'>='},
     * { oper:'IN', text:'in'},
     * { oper:'NI', text:'not in'},
     * { oper:'CN', text:'like'},
     * { oper:'NC', text:'not like'},
     * { oper:'NU', text:'is null'},
     * { oper:'NN', text:'is not null'},
     * { oper:'BLANK', text:'='''},
     * { oper:'NBLANK', text:'!='''}
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
