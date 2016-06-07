package com.fundevelop.framework.erp.frame.impl;

import com.fundevelop.framework.erp.frame.FrameDataModel;
import com.fundevelop.framework.erp.frame.datamodel.autocomplete.AutocompleteModel;
import com.fundevelop.framework.erp.frame.datamodel.datagrid.DataGridModel;
import com.fundevelop.framework.erp.frame.datamodel.jqgrid.JqGridModel;
import com.fundevelop.framework.erp.frame.datamodel.jstree.JsTreeModel;
import com.fundevelop.framework.erp.frame.datamodel.menu.MenuModel;
import com.fundevelop.framework.erp.frame.datamodel.tree.TreeModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 默认前端表示层框架数据模式处理类.
 * <p>描述:负责处理并转换数据模型</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 17:31
 */
@Component
public class DefaultDataModuleHandling {
    /**
     * 转换jqgrid数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> jqgrid(Class<OT> otClass, OID oid, HttpServletRequest request) {
        JqGridModel<OT,OID> model = new JqGridModel<>();
        model.setUserData(getParamter(request, JqGridModel.USERDATA_PARAM));

        return model;
    }

    /**
     * 转换DataGrid数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> datagrid(Class<OT> otClass, OID oid, HttpServletRequest request) {
        DataGridModel<OT,OID> model = new DataGridModel<>();

        model.setChecked(request.getParameter(DataGridModel.CHECKED_PARAM));
        model.setSelect(request.getParameter(DataGridModel.SELECT_PARAM));
        model.setUserData(request.getParameter(DataGridModel.USERDATA_PARAM));
        model.setKids(request.getParameter(DataGridModel.KIDS_PARAM));
        model.setParent(oid);

        return model;
    }

    /**
     * 转换Tree数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> tree(Class<OT> otClass, OID oid, HttpServletRequest request) {
        TreeModel<OT,OID> model = new TreeModel<>(oid);

        model.setOpen(request.getParameter(TreeModel.GLOBALOPEN_PARAM));
        model.setChecked(request.getParameter(TreeModel.CHECKED_PARAM));
        model.setSelect(request.getParameter(TreeModel.SELECT_PARAM));
        model.setUserData(request.getParameter(TreeModel.USERDATA_PARAM));
        model.setNoChild(request.getParameter(TreeModel.NOCHILD_PARAM));

        return model;
    }

    /**
     * 转换Tree数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> jstree(Class<OT> otClass, OID oid, HttpServletRequest request) {
        JsTreeModel<OT,OID> model = new JsTreeModel<>();

        model.setListAttrs(request.getParameter(JsTreeModel.LISTATTRS_PARAM));
        model.setNoChild(request.getParameter(JsTreeModel.NOCHILD_PARAM));
        model.setSelectAttrName(request.getParameter(JsTreeModel.SELECT_PARAM));

        return model;
    }

    /**
     * 转换Menu数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> menu(Class<OT> otClass, OID oid, HttpServletRequest request) {
        MenuModel<OT,OID> model = new MenuModel<>(oid, request.getContextPath());

        model.setChecked(request.getParameter(MenuModel.CHECKED_PARAM));
        model.setUserData(request.getParameter(MenuModel.USERDATA_PARAM));
        model.setNoChild(request.getParameter(MenuModel.NOCHILD_PARAM));

        return model;
    }

    /**
     * 转换Autocomplete数据模型.
     */
    public <OID extends Serializable,OT extends BaseEntity<OID>> FrameDataModel<OT,OID> autocomplete(Class<OT> otClass, OID oid, HttpServletRequest request) {
        return new AutocompleteModel<OT, OID>();
    }

    /**
     * 根据数据模型名称获取对应的排戏参数.
     */
    public String getOrder(String modelName, String orderStr, HttpServletRequest request) {
        if (JQGRID_MODEL.equalsIgnoreCase(modelName)) {
            String sidx = request.getParameter(JQGRID_ORDER_PARAM_NAME);
            String sord = request.getParameter(JQGRID_ORDER_BY_PARAM_NAME);
            String jqGridOrderStr = "";

            if (StringUtils.isNotBlank(sidx)) {
                jqGridOrderStr = sidx.trim();
            }
            if (StringUtils.isNotBlank(sord)) {
                if (StringUtils.isNotBlank(jqGridOrderStr)) {
                    jqGridOrderStr += ",";
                }

                jqGridOrderStr += sord.trim();
            }

            return jqGridOrderStr;
        }

        return null;
    }

    /**
     * 获取参数.
     */
    private Object getParamter(HttpServletRequest request, String name) {
        Object value = request.getParameter(name);

        if (value == null) {
            value = request.getAttribute(name);
        }

        return value;
    }

    private static final String JQGRID_MODEL = "jqgrid";
    private static final String JQGRID_ORDER_PARAM_NAME = "sidx";
    private static final String JQGRID_ORDER_BY_PARAM_NAME = "sord";
}
