package com.fundevelop.framework.erp.frame.datamodel.helps;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ognl.Ognl;
import ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * JSon Bean构造器.
 * <p>描述:辅助进行数据模型转换</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 17:52
 */
public abstract class JsonBeanBuild {
    /**
     * 构造函数.
     * @param filterProvider Jackson过滤器
     */
    public JsonBeanBuild(JacksonFilterProvider filterProvider) {
        this.filterProvider = filterProvider;
    }

    /**
     * 转换实体.
     * @param bean 待转换实体
     * @param data 转换后结果存放集合
     */
    public void convert(Object bean, Object data) {
        try {
            mapper.setAnnotationIntrospector(ai);
            mapper.setFilters(filterProvider);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.setTimeZone(TimeZone.getDefault());
            convertBean(mapper.readTree(mapper.writeValueAsString(bean)), bean, data);
        } catch (JsonProcessingException e) {
            logger.warn("转换实体失败", e);
        } catch (IOException e) {
            logger.warn("转换实体失败", e);
        } catch (OgnlException e) {
            logger.warn("转换实体失败", e);
        }
    }

    /**
     * 根据属性获取对应的节点.
     */
    protected JsonNode findNode(JsonNode rootNode, String property) {
        int pos = property.indexOf(".");

        if (pos != -1) {
            String p = property.substring(0,pos);
            String s = property.substring(pos+1);

            return findNode(rootNode.get(p),s);
        }

        return rootNode.get(property);
    }

    /**
     * 解析Bean中的属性.
     * @param rootNode 根节点
     * @param bean 要转换的Bean
     * @param data 转换后结果存放集合
     * @throws OgnlException
     */
    protected void convertBean(JsonNode rootNode, Object bean, Object data) throws OgnlException {
        String[] inPropertys = filterProvider.getInPropertys(getFilterId(bean.getClass()));

        if (inPropertys != null && inPropertys.length > 0) {
            for (String property : inPropertys) {
                JsonNode node = findNode(rootNode,property);

                if (node != null) {
                    if (node.isObject()) {
                        convertBean(node,Ognl.getValue(property, bean), data);
                    } else {
                        addData(data, property, node.asText());
                    }
                }
            }
        } else {
            Iterator<String> fieldNames = rootNode.fieldNames();

            for (; fieldNames.hasNext();) {
                String property = fieldNames.next();
                JsonNode node = findNode(rootNode,property);

                if (node != null) {
                    if (node.isObject()) {
                        convertBean(node, Ognl.getValue(property, bean), data);
                    } else {
                        addData(data, property, node.asText());
                    }
                }
            }
        }
    }

    /**
     * 添加数据.
     * @param data 转换后结果存放集合
     * @param name 属性名称
     * @param value 属性值
     */
    protected abstract void addData(Object data, String name,String value);

    /**
     * 获取Jackson过滤器.
     */
    public JacksonFilterProvider getFilterProvider() {
        return filterProvider;
    }

    /**
     * 设置Jackson过滤器.
     */
    public void setFilterProvider(JacksonFilterProvider filterProvider) {
        this.filterProvider = filterProvider;
    }

    /**
     * 获取实体中设置的过滤器ID.
     * @param clazz 实体类
     * @return 过滤器ID
     */
    public static final String getFilterId(Class clazz) {
        JsonFilter jsonFilter = (JsonFilter)clazz.getAnnotation(JsonFilter.class);

        if (jsonFilter != null) {
            return jsonFilter.value()+BeanFilter.FILTERSPLIT+clazz.getName();
        }

        return clazz.getName();
    }

    private AnnotationIntrospector ai = new JsonAnnotationIntrospector();

    /** Jackson过滤器. */
    private JacksonFilterProvider filterProvider = null;
    /** Json转换器. */
    private ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(JsonBeanBuild.class);
}
