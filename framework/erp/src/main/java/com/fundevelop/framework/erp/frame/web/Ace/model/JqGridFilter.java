package com.fundevelop.framework.erp.frame.web.Ace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * JqGrid查询参数模型.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 18:57
 */
public class JqGridFilter {
    private String groupOp;
    private List<JqGridFilterRule> rules = new ArrayList<>();

    public String getGroupOp() {
        return groupOp;
    }

    public void setGroupOp(String groupOp) {
        this.groupOp = groupOp;
    }

    public List<JqGridFilterRule> getRules() {
        return rules;
    }

    public void setRules(List<JqGridFilterRule> rules) {
        this.rules = rules;
    }
}
