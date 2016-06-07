package com.fundevelop.framework.erp.frame.web.Ace.model;

import org.apache.commons.lang3.StringUtils;

/**
 * JqGrid查询条件模型.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 18:58
 */
public class JqGridFilterRule {
    private String field;
    private String op;
    private Object data;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;

        // jqGrid的filterToolbar默认的op为true，自动转换为LIKE
        if (StringUtils.equals("true", op)) {
            this.op = "CN";
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
