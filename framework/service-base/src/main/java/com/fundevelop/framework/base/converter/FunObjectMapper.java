package com.fundevelop.framework.base.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 将日期转换为JSON.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/8/12 02:54
 */
public final class FunObjectMapper extends ObjectMapper {
    public FunObjectMapper() {
        Hibernate4Module module = new Hibernate4Module();
        module.configure(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION, false);
        this.setTimeZone(TimeZone.getDefault());
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModule(module)
                .registerModule(new JodaModule());
    }
}
