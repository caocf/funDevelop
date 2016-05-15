package com.fundevelop.framework.manager.jpa.query.dynamic;

import java.io.Serializable;

/**
 * 自定义枚举类型接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 17:00
 */
public interface EnumType extends Serializable {
    /**
     * 根据代码获取枚举类型.
     */
    EnumType getByCode(int code);

    /**
     * 获取代码.
     */
    public int getCode();
}