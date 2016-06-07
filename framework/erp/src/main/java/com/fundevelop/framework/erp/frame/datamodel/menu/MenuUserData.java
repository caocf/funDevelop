package com.fundevelop.framework.erp.frame.datamodel.menu;

import java.io.Serializable;

/**
 * 用户自定义数据.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:25
 */
public class MenuUserData implements Serializable {
    /** 自定义属性名称. */
    private String name;
    /** 自定义属性值. */
    private Object content;

    /**
     * 构造函数.
     */
    public MenuUserData(String name, Object content) {
        this.name = name;
        this.content = content;
    }

    /**
     * 获取自定义属性名称.
     */
    public String getName() {
        return name;
    }

    /**
     * 获取自定义属性值.
     */
    public Object getContent() {
        return content;
    }
}
