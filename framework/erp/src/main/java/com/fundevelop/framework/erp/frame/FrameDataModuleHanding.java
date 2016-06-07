package com.fundevelop.framework.erp.frame;

import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.framework.erp.frame.datamodel.helps.BeanFilter;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.framework.erp.utils.WebSearchHelper;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

/**
 * 前端表示层框架数据模式处理类.
 * <p>描述:负责处理并转换数据模型</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 15:33
 */
public class FrameDataModuleHanding {
    /** 前端表示层框架名称对应参数名称. */
    public static final String FRAME_PARAM = "E_frame";
    /** 前端表示层展示插件名称对应参数名称 */
    public static final String MODEL_PARAM = "E_model";

    /**
     * 根据数据模型名称获取对应的数据模型.
     * @param frameName 前端表示层框架名称
     * @param modelName 数据模型名称
     * @param otClass 模型使用的实体类
     * @param oid 模型使用的实体类ID类型
     * @param request http请求实例
     * @return 对应的数据模型
     */
    public static <OT extends BaseEntity<OID>, OID extends Serializable> FrameDataModel<OT, OID> getDataModel(String frameName, String modelName, Class<OT> otClass, OID oid, HttpServletRequest request) {
        FrameDataModel<OT, OID> dataModel = new DefaultDataModel<>();
        Object frameHandling = getFrameDataModuleHandling(frameName);

        if (frameHandling != null && StringUtils.isNotBlank(modelName)) {
            Method method = ReflectionUtils.findMethod(frameHandling.getClass(), modelName, Class.class, Serializable.class, HttpServletRequest.class);

            if (method != null) {
                dataModel = (FrameDataModel<OT, OID>) ReflectionUtils.invokeMethod(method, frameHandling, otClass, oid, request);
            }
        }

        buildFilter(dataModel, otClass, request);

        return dataModel;
    }

    /**
     * 根据数据模型名称获取对应的排戏参数.
     * @param frameName 前端表示层框架名称
     * @param modelName 数据模型名称
     * @param orderStr 前端框架对应的排序参数
     * @return 架构支持的排序参数
     */
    public static String getOrder(String frameName, String modelName, String orderStr, HttpServletRequest request) {
        Object frameHandling = getFrameDataModuleHandling(frameName);

        if (frameHandling != null) {
            Method method = ReflectionUtils.findMethod(frameHandling.getClass(), FRAME_ORDER_METHOD_NAME, String.class, String.class, HttpServletRequest.class);

            if (method != null) {
                return (String) ReflectionUtils.invokeMethod(method, frameHandling, modelName, orderStr, request);
            }
        }

        return null;
    }

    /**
     * 获取对应的前端表示层框架数据模型转换处理对象.
     * @return 对应的前端表示层框架数据模型转换处理对象
     */
    private static Object getFrameDataModuleHandling(String frameName) {
        if (StringUtils.isNotBlank(frameName)) {
            return SpringContextHolder.getBeanNotRequired(frameName+DATAMODULEHANDLING_SUF);
        }

        return null;
    }

    private static void buildFilter(BeanFilter bean, Class otClass, HttpServletRequest request) {
        Map<String, Object> filterMaps = WebSearchHelper.getParametersStartingWith(request, BeanFilter.filterPre, true);
        Map<String, String[]> filters = new TreeMap<>();

        for (Map.Entry<String, Object> entry : filterMaps.entrySet()) {
            // 过滤掉空值
            String filterPropertys = (String)entry.getValue();
            if (StringUtils.isBlank(filterPropertys)) {
                continue;
            }

            filters.put(entry.getKey(), filterPropertys.split(","));
        }

        for (Map.Entry<String, String[]> filter : filters.entrySet()) {
            bean.addFilter(otClass, filter.getKey(), filter.getValue());
        }

        filters = null;
    }


    /** 框架数据模型转换处理类后缀. */
    private static final String DATAMODULEHANDLING_SUF = "DataModuleHandling";
    /** 前端表示层框架获取排序字段方法名. */
    private static final String FRAME_ORDER_METHOD_NAME = "getOrder";

    private FrameDataModuleHanding(){}
}
