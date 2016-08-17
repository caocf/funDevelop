package com.fundevelop.plugin.sms.impl;

import com.fundevelop.plugin.sms.Sms;
import com.fundevelop.plugin.sms.SmsEventListener;
import com.fundevelop.plugin.sms.SmsUtils;
import com.fundevelop.plugin.sms.entity.SmsEntity;
import com.fundevelop.plugin.sms.manager.SmsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 默认短信事件监听器.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/8/17 17:38
 */
@Component
public class DefaultEventListener implements SmsEventListener {
    @Override
    public void sendSucessEvent(String smsId, String msgId, String channelCode) {
        SmsEntity smsEntity = smsManager.getByID(Long.parseLong(smsId, 10));

        if (smsEntity != null) {
            smsEntity.setStatus(SmsUtils.SENDED_STATUS);
            smsEntity.setRrid(msgId);
            smsEntity.setChannel(channelCode);
            smsEntity.setSendTiming(new Date());

            smsManager.save(smsEntity);
        }
    }

    @Override
    public void sendFailEvent(Sms sms, String channelCode, String message) {
        logger.warn("短信发送失败，失败原因：{}，短信通道：{}，短信：{}", message, channelCode, sms);
    }

    @Autowired
    private SmsManager smsManager;

    private Logger logger = LoggerFactory.getLogger(getClass());
}
