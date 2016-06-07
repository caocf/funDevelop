package com.fundevelop.framework.erp.frame.datamodel.tree;

import java.io.Serializable;
import java.util.List;

/**
 * Tree节点描述Bean.
 * <p>描述:存储Tree单个节点信息</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:27
 */
public class TreeItem<ID extends Serializable> implements Serializable {
    /** 节点ID. */
    private ID id;
    /** 节点名称. */
    private String text;
    /** 是否有子节点. */
    private boolean child;
    /** 自定义属性集合. */
    private List<TreeUserData> userdata;
    /** 节点是否处于选中状态. */
    private boolean select;
    /** 节点是否勾选. */
    private boolean checked;
    /** 是否打开节点. */
    private boolean open;

    /**
     * 构造函数.
     * @param id 节点ID
     * @param text 节点名称
     * @param child 是否有子节点
     * @param userdata 自定义属性
     */
    public TreeItem(ID id, String text, boolean child, List<TreeUserData> userdata) {
        this.id = id;
        this.text = text;
        this.child = child;
        this.userdata = userdata;
    }

    /**
     * 获取节点ID.
     */
    public ID getId() {
        return id;
    }

    /**
     * 获取节点名称.
     */
    public String getText() {
        return text;
    }

    /**
     * 获取是否有子节点.
     */
    public boolean isChild() {
        return child;
    }

    /**
     * 获取自定义属性集合.
     */
    public List<TreeUserData> getUserdata() {
        return userdata;
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
     * 获取打开状态.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * 设置打开状态.
     */
    public void setOpen(boolean open) {
        this.open = open;
    }
}
