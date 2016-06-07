package com.fundevelop.framework.erp.frame.datamodel.datagrid;

import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;

import java.util.Map;

/**
 * 将实体转换为DHtmlx DataGrid的自定义属性构造辅助类.
 * <p>描述:负责将实体Bean中的属性转换为Map集合</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:59
 */
class DataGridUserDataBuild extends JsonBeanBuild {
    /**
     * 构造函数.
     * @param filterProvider
     */
    public DataGridUserDataBuild(JacksonFilterProvider filterProvider) {
        super(filterProvider);
    }

    @Override
    protected void addData(Object data, String name, String value) {
        if (data instanceof Map) {
            ((Map)data).put(name, value);
        }
    }
}
