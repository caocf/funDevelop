package com.fundevelop.framework.erp.frame.datamodel.jqgrid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;

import java.io.Serializable;

/**
 * JqGrid数据Bean.
 * <p>描述:存储JqGrid数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 11:50
 */
public class JqGridModel<T extends BaseEntity<ID>, ID extends Serializable> extends DefaultDataModel<T, ID> {
    /** 自定义数据参数名称. */
    public static final String USERDATA_PARAM = "FP_UD";

    @Override
    @JsonProperty("records")
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    @JsonProperty("total")
    public int getTotalPages() {
        return super.getTotalPages();
    }

    private Object userData;

    public Object getUserData() {
        return userData;
    }
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    @Override
    @JsonProperty("rows")
    public Object getData() {
        return super.getData();
    }
}
