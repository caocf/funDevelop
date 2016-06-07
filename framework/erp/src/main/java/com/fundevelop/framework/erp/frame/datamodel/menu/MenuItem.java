package com.fundevelop.framework.erp.frame.datamodel.menu;

import java.io.Serializable;
import java.util.List;

/**
 * Menu节点描述Bean.
 * <p>描述:存储Menu单个节点信息</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:46
 */
public class MenuItem<ID extends Serializable> implements Serializable {
    /** 菜单ID. */
    private ID id;
    /** 菜单名称. */
    private String text;
    /** 是否有子菜单. */
    private boolean complex;
    /** 自定义属性集合. */
    private List<MenuUserData> userdata;
    /** 菜单是否勾选. */
    private boolean checked;
    /** 菜单链接地址. */
    private String href;

    /**
     * 构造函数.
     * @param id 菜单ID
     * @param text 菜单名称
     * @param href 菜单链接地址
     * @param complex 是否有子菜单
     * @param userdata 自定义属性
     */
    public MenuItem(ID id, String text, String href, boolean complex, List<MenuUserData> userdata) {
        this.id = id;
        this.text = text;
        this.href = href;
        this.complex = complex;
        this.userdata = userdata;
    }

    /**
     * 获取是否勾选.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 设置是否勾选.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * 获取菜单ID.
     */
    public ID getId() {
        return id;
    }

    /**
     * 获取菜单名称.
     */
    public String getText() {
        return text;
    }

    /**
     * 获取是否有子菜单.
     */
    public boolean isComplex() {
        return complex;
    }

    /**
     * 获取自定义属性集合.
     */
    public List<MenuUserData> getUserdata() {
        return userdata;
    }

    /**
     * 获取菜单链接地址.
     */
    public String getHref() {
        return href;
    }
}
