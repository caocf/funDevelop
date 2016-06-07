package com.fundevelop.framework.erp.frame.datamodel.helps;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * Jackson过滤器管理类.
 * <p>描述:负责管理Jackson过滤器</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 15:42
 */
public class JacksonFilterProvider extends SimpleFilterProvider {
    /**
     * 添加过滤器.
     * @param clazz 实体类
     * @param filterId 过滤器ID
     * @param propertys 过滤属性
     */
    public void addFilter(Class clazz, String filterId, String... propertys) {
        String[] filter = StringUtils.split(filterId, "_");

        if (filter != null && filter.length == 2) {
            if ("in".equalsIgnoreCase(filter[1])) {
                processPropertys(false,clazz,filter[0],propertys);
                inFilterMap.put(filter[0], propertys);
            } else {
                processPropertys(true,clazz,filter[0],propertys);
                outFilterMap.put(filter[0], propertys);
            }
        } else if (filter != null && filter.length == 1) {
            if ("in".equalsIgnoreCase(filterId)) {
                processPropertys(false,clazz,clazz.getName(),propertys);
                inFilterMap.put(clazz.getName(), propertys);
            } else if ("out".equalsIgnoreCase(filterId)) {
                processPropertys(true,clazz,clazz.getName(),propertys);
                outFilterMap.put(clazz.getName(), propertys);
            } else {
                processPropertys(true,clazz,filterId,propertys);
                outFilterMap.put(filterId, propertys);
            }
        }
    }

    @Override
    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
        String[] filterIds = ((String)filterId).split(BeanFilter.FILTERSPLIT);
        PropertyFilter f = null;

        if (filterIds != null && filterIds.length == 2) {
            f = _filtersById.get(filterIds[1]);

            if (f == null) {
                f = _filtersById.get(filterIds[0]);
            } else {
                //TODO 合并两个过滤器中的属性
            }
        } else if (filterIds != null && filterIds.length == 1) {
            f = _filtersById.get(filterId);
        }

        if (f == null) {
            f = _defaultFilter;
            if (f == null && _cfgFailOnUnknownId) {
                throw new IllegalArgumentException("No filter configured with id '"+filterId+"' (type "
                        +filterId.getClass().getName()+")");
            }
        }
        return f;
    }

    /**
     * 根据过滤器ID获取要保留的属性.
     * @param filterId 过滤器ID
     * @return 要保留的属性
     */
    public String[] getInPropertys(String filterId) {
        String[] filterIds = filterId.split(BeanFilter.FILTERSPLIT);

        if (filterIds != null && filterIds.length == 2) {
            if (inFilterMap.get(filterIds[1]) == null) {
                return inFilterMap.get(filterIds[0]);
            }

            return inFilterMap.get(filterIds[1]);
        }

        return inFilterMap.get(filterId);
    }

    /**
     * 处理属性中包含子属性的情况.
     */
    private void processPropertys (Boolean isExcept, Class clazz, String filterId, String... propertys) {
        Set<String> newPropertys = new HashSet<>(propertys.length);
        Map<String, List<String>> subMap = new HashMap<>(1);

        for (String property : propertys) {
            int pos = property.indexOf(".");

            if (pos != -1) {
                String p = property.substring(0,pos);
                String s = property.substring(pos+1);

                if (!newPropertys.contains(p)) {
                    newPropertys.add(p);
                }

                List<String> subpropertys = subMap.get(p);

                if (subpropertys == null) {
                    subpropertys = new ArrayList<>(1);
                }

                if (!subpropertys.contains(s)) {
                    subpropertys.add(s);
                }

                subMap.put(p, subpropertys);
            } else {
                if (!newPropertys.contains(property)) {
                    newPropertys.add(property);
                }
            }
        }

        if (isExcept) {
            addFilter(filterId, SimpleBeanPropertyFilter.serializeAllExcept(newPropertys));
        } else {
            addFilter(filterId, SimpleBeanPropertyFilter.filterOutAllExcept(newPropertys));
        }

        // 遍历子属性
        for (Iterator<String> subFilter = subMap.keySet().iterator(); subFilter.hasNext();) {
            String property = subFilter.next();
            Class subClass = BeanUtils.findPropertyType(property, new Class[]{clazz});
            String[] subpropertys = new String[subMap.get(property).size()];
            subMap.get(property).toArray(subpropertys);

            processPropertys (isExcept, subClass, subClass.getName(), subpropertys);
        }
    }

    /**
     * 构造函数.
     */
    public JacksonFilterProvider() {
        super();
        setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(""));
    }

    /** 存储包含的过滤属性集合. */
    private Map<String, String[]> inFilterMap = new HashMap<>(1);
    /** 存储排除的过滤属性集合. */
    private Map<String, String[]> outFilterMap = new HashMap<>(1);
}
