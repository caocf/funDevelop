package com.fundevelop.framework.erp.frame.datamodel.tree;

import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;

import java.util.Collection;

/**
 * 将实体转换为DHtmlx Tree构造辅助类.
 * <p>描述:负责将实体Bean中的属性转换为userdata</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:23
 */
public class TreeItemBuild extends JsonBeanBuild {
    /**
     * 构造函数.
     * @param filterProvider
     */
    public TreeItemBuild(JacksonFilterProvider filterProvider) {
        super(filterProvider);
    }

    @Override
    protected void addData(Object data, String name, String value) {
        if (data instanceof Collection) {
            ((Collection)data).add(new TreeUserData(name, value));
        }
    }
}
