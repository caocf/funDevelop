package com.fundevelop.persistence.entity.hibernate.id;

import ognl.OgnlOps;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * 使用系统当前时间作为主键.
 * <p>描述:使用系统当前时间毫秒数+随机数作为主键</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/8 15:08
 */
public class TimeMillisGenerator implements IdentifierGenerator, Configurable {
    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        this.type = type.getReturnedClass();
        String parameters = params.getProperty( RANDOMLENGTH );

        if (parameters != null) {
            try {
                randomLength = Integer.parseInt(parameters, 10);
            } catch (NumberFormatException ne){
                randomLength = 0;
            }
        }
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        String id = String.valueOf(System.currentTimeMillis());

        if (randomLength > 0) {
            id += String.valueOf((int)(Math.random()*Math.pow(10,randomLength)));
        }

        return (Serializable)OgnlOps.convertValue(id, type);
    }

    /** ID数据类型. */
    private Class<Serializable> type;
    /** 随机数长度，默认为0. */
    private int randomLength = 0;

    /** 自定随机数长度参数名称. */
    public static final String RANDOMLENGTH = "random";
}
