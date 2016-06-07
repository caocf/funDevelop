package com.fundevelop.framework.erp.frame.datamodel.helps;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Jackson根据注解默认处理.
 * <p>描述:修改获取过滤器ID方式为过滤器ID+分隔符+实体类名</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 17:53
 */
public class JsonAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public Object findFilterId(Annotated a) {
        Object filterId = super.findFilterId(a);

        if (filterId != null) {
            return filterId+BeanFilter.FILTERSPLIT+a.getName();
        }

        return a.getName();
    }
}
