package com.fundevelop.framework.erp.frame.datamodel.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.List;

/**
 * Tree数据Bean.
 * <p>描述:存储Tree数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 18:02
 */
public class TreeModel <T extends BaseEntity<ID>,ID extends Serializable> extends DefaultDataModel<T, ID> {
    /** 自定义数据参数名称. */
    public static final String USERDATA_PARAM = "FP_UD";
    /** 节点展开参数名称. */
    public static final String GLOBALOPEN_PARAM = "FP_OPEN";
    /** 选中节点参数名称. */
    public static final String SELECT_PARAM = "FP_SELECT";
    /** 勾选节点参数名称. */
    public static final String CHECKED_PARAM = "FP_CHECKED";
    /** 默认没有子节点参数名称. */
    public static final String NOCHILD_PARAM = "FP_NOCHILD";

    /**
     * 构造函数.
     * @param id 当前节点ID
     */
    public TreeModel(ID id) {
        this.id = id;
    }

    @Override
    @JsonProperty("item")
    public Object getData() {
        return super.getData();
    }

    /**
     * 获取当前节点ID.
     */
    public ID getId() {
        return id;
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
            data = new ArrayList<TreeItem<ID>>(dataList.size());
            TreeItemBuild build = new TreeItemBuild(null);

            for (T bean : dataList) {
                ((List<TreeItem<ID>>)data).add(toTreeItem(bean,build));
            }

            build = null;
        }
    }

    /**
     * 将实体转换成树节点.
     */
    private TreeItem<ID> toTreeItem(T bean, TreeItemBuild build) {
        if (textAttrName == null) {
            findPropertys(bean);
        }

        boolean child = !noChild;
        List<TreeUserData> userdata = null;

        if (!org.apache.commons.lang3.StringUtils.isBlank(userData)) {
            if (build.getFilterProvider() == null) {
                build.setFilterProvider(getUserDataFilter(bean.getClass()));
            }
            if (build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())) != null
                    && build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length > 0) {
                userdata = new ArrayList<TreeUserData>(build.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length);
                build.convert(bean, userdata);
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
            TreeItem<ID> item = new TreeItem<>(bean.getId(), Ognl.getValue(textAttrName, bean).toString(), child, userdata);
            item.setOpen(open);
            item.setSelect(isSelected(bean.getId()));
            item.setChecked(isChecked(bean.getId()));

            return item;
        } catch (OgnlException e) {
            throw new RuntimeException("从实体类["+bean.getClass()+"."+textAttrName+"]中获取节点名称失败",e);
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
     * 验证给定ID是否在选中列表中.
     */
    private boolean isSelected(ID id) {
        if (!org.apache.commons.lang3.StringUtils.isBlank(select)) {
            if ((","+select+",").indexOf(","+id+",") != -1) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取自定义数据过滤器.
     */
    private JacksonFilterProvider getUserDataFilter(Class clazz) {
        JacksonFilterProvider filterProvider = new JacksonFilterProvider();

        if (org.apache.commons.lang3.StringUtils.isNotBlank(userData)) {
            filterProvider.addFilter(clazz,"in",userData.split(","));
        }

        return filterProvider;
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
        }

        if (textAttrName == null || "".equals(textAttrName.trim())){
            throw new RuntimeException("必须使用F_in指定实体中的那个属性用来作为数节点的显示名称");
        }
    }

    /**
     * 设置自定义属性.
     */
    public void setUserData(String userData) {
        this.userData = userData;
    }

    /**
     * 设置默认展开.
     */
    public void setOpen(String open) {
        this.open = StringUtils.isBooleanTrue(open);
    }

    /**
     * 设置选中的节点ID.
     */
    public void setSelect(String select) {
        this.select = select;
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



    /** 当前节点ID. */
    private ID id;

    /** 自定义属性. */
    private String userData;
    /** 所有节点是否默认展开. */
    private boolean open;
    /** 选中的节点ID. */
    private String select;
    /** 勾选的节点ID. */
    private String checked;
    /** 是否默认没有子节点. */
    private boolean noChild;

    /** 节点名称属性名. */
    private String textAttrName;
    /** 是否有子节点属性名. */
    private String childAttrName;
}
