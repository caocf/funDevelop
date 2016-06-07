package com.fundevelop.framework.erp.frame.datamodel.autocomplete;

import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;

import java.util.Collection;

/**
 * 将实体转换为autocomplete的构造辅助类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 18:19
 */
class AutocompleteBuild extends JsonBeanBuild {
    /**
     * 构造函数.
     * @param filterProvider Jackson过滤器
     */
    public AutocompleteBuild(JacksonFilterProvider filterProvider){
        super(filterProvider);
    }

    @Override
    protected void addData(Object data, String name, String value) {
        if(data instanceof Collection) {
            ((Collection) data).add(value);
        }
    }
}
