package com.fundevelop.framework.erp.frame.datamodel.datagrid;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DataGrid行数据存储Bean.
 * <p>描述:存储DataGrid一行的数据</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:56
 */
class DataGridRow<ID extends Serializable> implements Serializable {
    /** 记录ID. */
    private ID id;
    /** 记录数据. */
    private List<String> data;
    /** 自定义属性集合. */
    private Map<String,String> userdata;
    /** 节点是否处于选中状态. */
    private boolean select;
    /** 节点是否勾选. */
    private boolean checked;
    /** 是否有子属性. */
    private String xmlkids = "";

    /**
     * 构造函数.
     */
    public DataGridRow(ID id, List<String> data, Map<String,String> userdata) {
        this.id = id;
        this.data = data;
        this.userdata = userdata;
    }

    /**
     * <p>获取属性id.</p>
     * @return ID
     */
    public ID getId() {
        return id;
    }

    /**
     * <p>获取属性data.</p>
     * @return ArrayList<String>
     */
    public List<String> getData() {
        return data;
    }

    /**
     * 获取选中状态.
     */
    public boolean isSelect() {
        return select;
    }

    /**
     * 设置选中状态.
     */
    public void setSelect(boolean select) {
        this.select = select;
    }

    /**
     * 获取勾选状态.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 设置勾选状态.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * 获取自定义属性集合.
     */
    public Map<String,String> getUserdata() {
        return userdata;
    }

    /**
     * 获取属性xmlkids.
     */
    public String getXmlkids() {
        return xmlkids;
    }

    /**
     * 设置属性xmlkids.
     */
    public void setXmlkids(String xmlkids) {
        this.xmlkids = xmlkids;
    }
}
