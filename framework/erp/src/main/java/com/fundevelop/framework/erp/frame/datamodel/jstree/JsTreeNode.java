package com.fundevelop.framework.erp.frame.datamodel.jstree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * JSTree节点描述Bean.
 * <p>描述:存储JSTree单个节点信息</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:35
 */
public class JsTreeNode<ID extends Serializable> implements Serializable {
    /** 节点ID. */
    private ID id;
    /** 节点名称. */
    private String text;
    /** 是否有子节点. */
    private Boolean children;
    private String icon;
    private String type;

    private Map<String, Object> state = new HashMap<String, Object>(1);
    private Map<String, Object> listAttrs = new HashMap<String, Object>(1);
    private Map<String, Object> linkAttrs = new HashMap<String, Object>(1);

    /**
     * 构造函数.
     * @param id 节点ID
     * @param text 节点名称
     * @param children 是否有子节点
     * @param icon 节点图标
     */
    public JsTreeNode(ID id, String text, boolean children, String icon) {
        this.id = id;
        this.text = text;
        this.children = children;
        this.icon = icon;
    }

    /**
     * 获取节点ID.
     */
    public ID getId() {
        return id;
    }

    /**
     * 设置节点ID.
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * 获取节点名称.
     */
    public String getText() {
        return text;
    }

    /**
     * 设置节点名称.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取是否有子节点.
     */
    public Boolean getChildren() {
        return children;
    }

    /**
     * 设置是否有子节点.
     */
    public void setChildren(Boolean children) {
        this.children = children;
    }

    /**
     * 获取属性icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置属性icon.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取属性type.
     */
    public String getType() {
        return type;
    }

    /**
     * 设置属性type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取属性state.
     */
    public Map<String, Object> getState() {
        return state;
    }

    /**
     * 设置属性state.
     */
    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    /**
     * 获取属性listAttrs.
     */
    public Map<String, Object> getListAttrs() {
        return listAttrs;
    }

    /**
     * 设置属性listAttrs.
     */
    public void setListAttrs(Map<String, Object> listAttrs) {
        this.listAttrs = listAttrs;
    }

    /**
     * 获取属性linkAttrs.
     */
    public Map<String, Object> getLinkAttrs() {
        return linkAttrs;
    }

    /**
     * 设置属性linkAttrs.
     */
    public void setLinkAttrs(Map<String, Object> linkAttrs) {
        this.linkAttrs = linkAttrs;
    }
}
