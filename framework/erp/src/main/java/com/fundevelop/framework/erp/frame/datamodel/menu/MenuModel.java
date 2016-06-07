package com.fundevelop.framework.erp.frame.datamodel.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fundevelop.commons.utils.StringUtils;
import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;
import com.fundevelop.framework.erp.frame.datamodel.tree.TreeItemBuild;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu数据Bean.
 * <p>描述:存储Menu数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 20:41
 */
public class MenuModel<T extends BaseEntity<ID>,ID extends Serializable> extends DefaultDataModel<T, ID> {
    /** 自定义数据参数名称. */
    public static final String USERDATA_PARAM = "FP_UD";
    /** 勾选节点参数名称. */
    public static final String CHECKED_PARAM = "FP_CHECKED";
    /** 默认没有子节点参数名称. */
    public static final String NOCHILD_PARAM = "FP_NOCHILD";

    /**
     * 构造函数.
     * @param parentId 上级菜单ID
     * @param contextPath 项目上下文地址
     */
    public MenuModel(ID parentId, String contextPath) {
        this.parentId = parentId;
        this.contextPath = contextPath;
    }

    /**
     * 获取上级节点ID.
     */
    public ID getParentId() {
        return parentId;
    }

    @Override
    @JsonProperty("item")
    public Object getData() {
        return super.getData();
    }

    @Override
    @JsonIgnore
    public int getPage() {
        return super.getPage();
    }

    @Override
    @JsonIgnore
    public int getPageSize() {
        return super.getPageSize();
    }

    @Override
    @JsonIgnore
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    @JsonIgnore
    public int getTotalPages() {
        return super.getTotalPages();
    }

    @Override
    public void setData(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            data = new ArrayList<MenuItem<ID>>(dataList.size());
            TreeItemBuild build = new TreeItemBuild(null);

            for (T bean : dataList) {
                ((List<MenuItem<ID>>)data).add(toMenuItem(bean,build));
            }

            build = null;
        }
    }

    /**
     * 将实体转换成菜单节点.
     */
    private MenuItem<ID> toMenuItem(T bean, TreeItemBuild build) {
        if (textAttrName == null) {
            findPropertys(bean);
        }

        boolean child = !noChild;
        String href = null;
        List<MenuUserData> userdata = null;

        if (!org.apache.commons.lang3.StringUtils.isBlank(userData)) {
            if (build.getFilterProvider() == null) {
                build.setFilterProvider(getUserDataFilter(bean.getClass()));
            }
            if (build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())) != null
                    && build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length > 0) {
                userdata = new ArrayList<MenuUserData>(build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length);
                build.convert(bean, userdata);
            }
        }

        if (hrefAttrName != null && !"".equals(hrefAttrName.trim())) {
            try {
                href = contextPath+(String)Ognl.getValue(hrefAttrName, bean, String.class);
            } catch (OgnlException e) {
                throw new RuntimeException("从实体类["+bean.getClass()+"."+hrefAttrName+"]中获取菜单链接地址属性失败",e);
            }
        }

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

        try {
            MenuItem<ID> item = new MenuItem<ID>(bean.getId(), Ognl.getValue(textAttrName, bean).toString(), href, child, userdata);
            item.setChecked(isChecked(bean.getId()));

            return item;
        } catch (OgnlException e) {
            throw new RuntimeException("从实体类["+bean.getClass()+"."+textAttrName+"]中获取菜单名称失败",e);
        }
    }

    /**
     * 验证给定ID是否在勾选列表中.
     */
    private boolean isChecked(ID id) {
        if (!org.apache.commons.lang3.StringUtils.isBlank(checked)) {
            if ((","+checked+",").indexOf(","+id+",") != -1) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从实体中获取菜单名称及菜单链接地址属性.
     */
    private void findPropertys(T bean) {
        String[] inPropertys = getFilter().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass()));

        if (inPropertys != null && inPropertys.length > 0) {
            textAttrName = inPropertys[0];

            if (inPropertys.length > 1) {
                hrefAttrName = inPropertys[1];
            }
            if (inPropertys.length > 2) {
                childAttrName = inPropertys[2];
            }
        }

        if (textAttrName == null || "".equals(textAttrName.trim())){
            throw new RuntimeException("必须使用F_in指定实体中的那个属性用来作为数节点的显示名称");
        }
    }

    /**
     * 获取自定义数据过滤器.
     */
    private JacksonFilterProvider getUserDataFilter(Class clazz) {
        JacksonFilterProvider filterProvider = new JacksonFilterProvider();

        if (!org.apache.commons.lang3.StringUtils.isBlank(userData)) {
            filterProvider.addFilter(clazz,"in",userData.split(","));
        }

        return filterProvider;
    }

    /**
     * 设置自定义属性.
     */
    public void setUserData(String userData) {
        this.userData = userData;
    }

    /**
     * 设置勾选的节点ID.
     */
    public void setChecked(String checked) {
        this.checked = checked;
    }

    /**
     * 设置默认没有子节点.
     */
    public void setNoChild(String noChild) {
        this.noChild = StringUtils.isBooleanTrue(noChild);
    }

    /** 上级节点ID. */
    private ID parentId;

    /** 自定义属性. */
    private String userData;
    /** 勾选的节点ID. */
    private String checked;
    /** 是否默认没有子节点. */
    private boolean noChild;

    /** 节点名称属性名. */
    private String textAttrName;
    /** 菜单链接地址属性名. */
    private String hrefAttrName;
    /** 是否有子节点属性名. */
    private String childAttrName;

    private String contextPath = "";
}
