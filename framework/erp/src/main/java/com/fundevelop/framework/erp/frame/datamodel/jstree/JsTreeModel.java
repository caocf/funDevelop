package com.fundevelop.framework.erp.frame.datamodel.jstree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fundevelop.commons.utils.StringUtils;
import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSTree数据Bean.
 * <p>描述:存储JSTree数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:30
 */
public class JsTreeModel<T extends BaseEntity<ID>,ID extends Serializable> extends DefaultDataModel<T, ID> {
    /** 自定义数据参数名称. */
    public static final String LISTATTRS_PARAM = "FP_LA";
    /** 默认没有子节点参数名称. */
    public static final String NOCHILD_PARAM = "FP_NOCHILD";
    /** 选中节点参数名称. */
    public static final String SELECT_PARAM = "FP_SELECT";

    @Override
    public void setData(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            data = new ArrayList<JsTreeNode<ID>>(dataList.size());
            JsTreeNodeBuild build = new JsTreeNodeBuild(null);

            for (T bean : dataList) {
                ((List<JsTreeNode<ID>>)data).add(toTreeItem(bean,build));
            }

            build = null;
        } else if (dataList == null || dataList.isEmpty()) {
            data = new ArrayList<JsTreeNode<ID>>(1);
        }
    }

    /**
     * 将实体转换成树节点.
     */
    private JsTreeNode<ID> toTreeItem(T bean, JsTreeNodeBuild build) {
        if (textAttrName == null) {
            findPropertys(bean);
        }

        boolean child = !noChild;

        if (childAttrName != null && !"".equals(childAttrName.trim())) {
            try {
                Class<?> toType = BeanUtils.findPropertyType(childAttrName, new Class[]{bean.getClass()});

                if (toType == Boolean.class) {
                    child = (Boolean)Ognl.getValue(childAttrName, bean, toType);
                } else {
                    child = Long.valueOf(Ognl.getValue(childAttrName, bean, toType).toString()) > 0;
                }
            } catch (OgnlException e) {
                throw new RuntimeException("从实体类["+bean.getClass()+"."+childAttrName+"]中获取节点是否有子节点属性失败",e);
            }
        }

        String icon = null;

        if (iconAttrName != null && !"".equals(iconAttrName.trim())) {
            try {
                icon = Ognl.getValue(iconAttrName, bean).toString();
            } catch (OgnlException e) {
                throw new RuntimeException("从实体类["+bean.getClass()+"."+iconAttrName+"]中获取节点图标属性失败",e);
            }
        }

        Map<String, Object> listAttrsMap = null;

        if (org.apache.commons.lang3.StringUtils.isNotBlank(listAttrs)) {
            if (build.getFilterProvider() == null) {
                build.setFilterProvider(getListAttrsFilter(bean.getClass()));
            }
            if (build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())) != null
                    && build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length > 0) {
                listAttrsMap = new HashMap<String, Object>(build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length);
                build.convert(bean, listAttrsMap);
            }
        }

        Boolean selected = null;

        if (!child) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(selectAttrName)) {
                try {
                    Class<?> toType = BeanUtils.findPropertyType(selectAttrName, new Class[]{bean.getClass()});

                    if (toType == Boolean.class) {
                        selected = (Boolean) Ognl.getValue(selectAttrName, bean, toType);
                    } else {
                        selected = StringUtils.isBooleanTrue(Ognl.getValue(selectAttrName, bean, toType).toString());
                    }
                } catch (OgnlException e) {
                    throw new RuntimeException("从实体类[" + bean.getClass() + "." + selectAttrName + "]中获取节点是否选中属性失败", e);
                }
            }
        }

        try {
            JsTreeNode<ID> item = new JsTreeNode<ID>(bean.getId(), Ognl.getValue(textAttrName, bean).toString(), child, icon);
            item.setListAttrs(listAttrsMap);

            if (selected != null) {
                item.getState().put("selected", selected);
            }

            return item;
        } catch (OgnlException e) {
            throw new RuntimeException("从实体类[" + bean.getClass() + "." + textAttrName + "]中获取节点名称失败", e);
        }
    }

    /**
     * 从实体中获取节点名称及是否存在子节点属性.
     */
    private void findPropertys(T bean) {
        String[] inPropertys = getFilter().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass()));

        if (inPropertys != null && inPropertys.length > 0) {
            textAttrName = inPropertys[0];

            if (inPropertys.length > 1) {
                childAttrName = inPropertys[1];
            }
            if (inPropertys.length > 2) {
                iconAttrName = inPropertys[2];
            }
        }

        if (textAttrName == null || "".equals(textAttrName.trim())){
            throw new RuntimeException("必须使用F_in指定实体中的那个属性用来作为数节点的显示名称");
        }
    }

    /**
     * 获取自定义数据过滤器.
     */
    @SuppressWarnings("rawtypes")
    private JacksonFilterProvider getListAttrsFilter(Class clazz) {
        JacksonFilterProvider filterProvider = new JacksonFilterProvider();

        if (org.apache.commons.lang3.StringUtils.isNotBlank(listAttrs)) {
            filterProvider.addFilter(clazz,"in",listAttrs.split(","));
        }

        return filterProvider;
    }

    @Override
    @JsonIgnore
    public boolean isUseDataOfRoot() {
        return true;
    }

    /**
     * 设置默认没有子节点.
     */
    public void setNoChild(String noChild) {
        this.noChild = StringUtils.isBooleanTrue(noChild);
    }

    /**
     * 设置自定义属性.
     */
    public void setListAttrs(String listAttrs) {
        this.listAttrs = listAttrs;
    }

    /**
     * 设置是否选中的节点属性名.
     */
    public void setSelectAttrName(String selectAttrName) {
        this.selectAttrName = selectAttrName;
    }

    /** 自定义属性. */
    private String listAttrs;
    /** 是否默认没有子节点. */
    private boolean noChild;

    /** 节点名称属性名. */
    private String textAttrName;
    /** 节点图标属性名. */
    private String iconAttrName;
    /** 是否有子节点属性名. */
    private String childAttrName;
    /** 是否选中的节点属性名. */
    private String selectAttrName;
}
