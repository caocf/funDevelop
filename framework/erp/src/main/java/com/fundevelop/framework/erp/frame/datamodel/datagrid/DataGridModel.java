package com.fundevelop.framework.erp.frame.datamodel.datagrid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fundevelop.commons.utils.StringUtils;
import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonFilterProvider;
import com.fundevelop.framework.erp.frame.datamodel.helps.JsonBeanBuild;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataGrid数据Bean.
 * <p>描述:存储DataGrid数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 17:49
 */
public class DataGridModel <T extends BaseEntity<ID>,ID extends Serializable> extends DefaultDataModel<T, ID> {
    /** 是否开启TreeGrid. */
    public static final String KIDS_PARAM = "FP_KIDS";
    /** 选中节点参数名称. */
    public static final String SELECT_PARAM = "FP_SELECT";
    /** 勾选节点参数名称. */
    public static final String CHECKED_PARAM = "FP_CHECKED";
    /** 自定义数据参数名称. */
    public static final String USERDATA_PARAM = "FP_UD";

    @Override
    @JsonProperty("rows")
    public List<DataGridRow<ID>> getData() {
        return (List<DataGridRow<ID>>)data;
    }

    /**
     * 获取当前记录位置.
     */
    public long getPos() {
        return (getPage()-1)*getPageSize();
    }

    @Override
    @JsonProperty("total_count")
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    public void setData(List<T> dataList) {
        // 将数据集合转换为数组集合
        if (dataList != null && dataList.size() > 0) {
            data = new ArrayList<DataGridRow<ID>>(dataList.size());
            DataGridRowBuild build = new DataGridRowBuild(getFilter());
            DataGridUserDataBuild userDataBuild = new DataGridUserDataBuild(null);

            for (T bean : dataList) {
                ((List<DataGridRow<ID>>)data).add(toGridRow(bean, build, userDataBuild));
            }

            build = null;
            userDataBuild = null;
            this.pageSize = dataList.size();
            this.total = this.pageSize;
        }
    }

    /**
     * 将实体转换成Grid行.
     */
    private DataGridRow<ID> toGridRow(T bean, DataGridRowBuild build, DataGridUserDataBuild userDataBuild) {
        Map<String, String> userdata = null;

        if (!org.apache.commons.lang3.StringUtils.isBlank(userData)) {
            if (userDataBuild.getFilterProvider() == null) {
                userDataBuild.setFilterProvider(getUserDataFilter(bean.getClass()));
            }

            if (userDataBuild.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())) != null
                    && userDataBuild.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length > 0) {
                userdata = new HashMap<>(userDataBuild.getFilterProvider().getInPropertys(JsonBeanBuild.getFilterId(bean.getClass())).length);
                userDataBuild.convert(bean, userdata);
            }
        }

        List<String> data = new ArrayList<String>(6);
        build.convert(bean, data);

        DataGridRow<ID> gridRow = new DataGridRow<ID>(bean.getId(), data, userdata);
        gridRow.setSelect(isSelected(bean.getId()));
        gridRow.setChecked(isChecked(bean.getId()));

        if (kids) {
            gridRow.setXmlkids("1");
        }

        return gridRow;
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
     * 获取自定义数据过滤器.
     */
    @SuppressWarnings("rawtypes")
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
     * 获取属性parent.
     */
    public ID getParent() {
        return parent;
    }

    /**
     * 设置属性parent.
     */
    public void setParent(ID parent) {
        this.parent = parent;
    }

    /**
     * 设置属性kids.
     */
    public void setKids(String kids) {
        this.kids = StringUtils.isBooleanTrue(kids);
    }

    /** 自定义属性. */
    private String userData;
    /** 选中的节点ID. */
    private String select;
    /** 勾选的节点ID. */
    private String checked;

    /** 上级节点ID. */
    private ID parent;
    /** 启用TreeGrid模型. */
    private boolean kids = false;
}
