package com.fundevelop.framework.manager.jpa.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

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
    /** or 条件 */
    private List<SearchFilter> orFilters = null;

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

    /**
     * 是否有OR条件.
     * @return
     */
    public boolean hasOrFilter() {
        return (orFilters!=null&&!orFilters.isEmpty());
    }

    /**
     * 增加OR条件.
     * @param fieldName 字段名称
     * @param operator 比较符
     * @param value 字段值
     */
    public void or(String fieldName, Operator operator, Object value) {
        or(new SearchFilter(fieldName, operator, value));
    }

    /**
     * 增加OR条件.
     * @param filter 查询条件
     */
    public void or(SearchFilter filter) {
        if (orFilters == null) {
            orFilters = new ArrayList<>();
        }

        orFilters.add(filter);
    }

    public List<SearchFilter> getOrFilters() {
        return orFilters;
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
