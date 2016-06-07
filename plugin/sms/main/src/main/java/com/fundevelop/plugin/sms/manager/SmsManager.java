package com.fundevelop.plugin.sms.manager;

import com.fundevelop.framework.manager.jpa.AbstractManager;
import com.fundevelop.plugin.sms.dao.SmsDao;
import com.fundevelop.plugin.sms.entity.SmsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 短信信息管理类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 21:39
 */
@Component
public class SmsManager extends AbstractManager<SmsEntity, Long, SmsDao> {
    /**
     * 获取待发送短信.
     */
    public List<SmsEntity> findWaitSendSms() {
        return getDao().findWaitSendSms();
    }

    /**
     * 获取待发送短信.
     */
    public List<SmsEntity> findWaitSendSms(String system) {
        return getDao().findWaitSendSms(system);
    }

    /**
     * 获取待发送短信.
     */
    public List<SmsEntity> findWaitSendSms(String system, String module) {
        return getDao().findWaitSendSms(system, module);
    }

    @Override
    protected SmsDao getDao() {
        return dao;
    }

    @Autowired
    private SmsDao dao;
}
