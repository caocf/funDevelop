package com.fundevelop.framework.base.model;

import java.io.Serializable;

/**
 * Select OptionModel.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/14 12:54
 */
public class OptionModel implements Serializable {
    private Object id;
    private Object value;
    private boolean selected;

    public OptionModel(Object id, Object value, boolean selected) {
        this.id = id;
        this.value = value;
        this.selected = selected;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
