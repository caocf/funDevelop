package com.fundevelop.plugin.sms;


import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.plugin.sms.entity.SmsEntity;
import com.fundevelop.plugin.sms.manager.SmsManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 短信工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 21:42
 */
public class SmsUtils {
    public static Long sendSms(String system, String module, String phone, String content) {
        return sendSms(system, module, DEFAULT_COUNTCODE, phone, content, 0, null, null);
    }

    public static Long sendSms(String system, String module, String countryCode, String phone, String content) {
        return sendSms(system, module, countryCode, phone, content, 0, null, null);
    }

    public static Long sendSms(String system, String module, String phone, String content, int priority) {
        return sendSms(system, module, DEFAULT_COUNTCODE, phone, content, priority, null, null);
    }

    public static Long sendSms(String system, String module, String countryCode, String phone, String content, int priority) {
        return sendSms(system, module, countryCode, phone, content, priority, null, null);
    }

    public static Long sendSms(String system, String module, String phone, String content, Date sendTiming) {
        return sendSms(system, module, DEFAULT_COUNTCODE, phone, content, 0, sendTiming, null);
    }

    public static Long sendSms(String system, String module, String countryCode, String phone, String content, Date sendTiming) {
        return sendSms(system, module, countryCode, phone, content, 0, sendTiming, null);
    }

    public static Long sendSms(String system, String module, String phone, String content, int priority, Date sendTiming) {
        return sendSms(system, module, DEFAULT_COUNTCODE, phone, content, priority, sendTiming, null);
    }

    public static Long sendSms(String system, String module, String countryCode, String phone, String content, int priority, Date sendTiming) {
        return sendSms(system, module, countryCode, phone, content, priority, sendTiming, null);
    }

    public static Long sendSms(String system, String module, String phone, String content, int priority, Date sendTiming, String memo) {
        return sendSms(system, module, DEFAULT_COUNTCODE, phone, content, priority, sendTiming, memo);
    }

    public static Long sendSms(String system, String module, String countryCode, String phone, String content, int priority, Date sendTiming, String memo) {
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(content)) {
            SmsEntity sms = new SmsEntity();
            sms.setCountryCode(countryCode);
            sms.setPhone(phone);
            sms.setContent(content);
            sms.setPriority(priority);
            sms.setSendTiming(sendTiming);
            sms.setSystem(PropertyUtil.get("service.name"));
            sms.setSystem(system);
            sms.setModule(module);
            sms.setMemo(memo);
            sms.setCreateTime(new Date());
            sms.setStatus(WAITING_SEND_STATUS);

            SmsManager smsManager = SpringContextHolder.getBean(SmsManager.class);

            if (smsManager == null) {
                throw new RuntimeException("系统找不到短信发送类实例");
            }

            smsManager.save(sms);

            return sms.getId();
        }

        return null;
    }

    public static SmsEntity prepareSms(String system, String module, String countryCode, String phone, String content, int priority, Date sendTiming, String memo) {
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(content)) {
            SmsEntity sms = new SmsEntity();
            sms.setCountryCode(countryCode);
            sms.setPhone(phone);
            sms.setContent(content);
            sms.setPriority(priority);
            sms.setSendTiming(sendTiming);
            sms.setSystem(PropertyUtil.get("service.name"));
            sms.setSystem(system);
            sms.setModule(module);
            sms.setMemo(memo);
            sms.setCreateTime(new Date());
            sms.setStatus(PREPARE_STATUS);

            SmsManager smsManager = SpringContextHolder.getBean(SmsManager.class);

            if (smsManager == null) {
                throw new RuntimeException("系统找不到短信发送类实例");
            }

            smsManager.save(sms);

            return sms;
        }

        return null;
    }

    public static void send(SmsEntity sms) {
        if (sms != null) {
            if (PREPARE_STATUS.equals(sms.getStatus())) {
                sms.setStatus(WAITING_SEND_STATUS);

                SmsManager smsManager = SpringContextHolder.getBean(SmsManager.class);

                if (smsManager == null) {
                    throw new RuntimeException("系统找不到短信发送类实例");
                }

                smsManager.save(sms);
            }
        }
    }

    public static SmsEntity getSms(Long smsId) {
        if (smsId != null) {
            SmsManager smsManager = SpringContextHolder.getBean(SmsManager.class);

            if (smsManager == null) {
                throw new RuntimeException("系统找不到短信发送类实例");
            }

            return smsManager.getByID(smsId);
        }

        return null;
    }

    public static final String PREPARE_STATUS = "0";
    public static final String WAITING_SEND_STATUS = "1";
    public static final String SENDED_STATUS = "2";

    private static final String DEFAULT_COUNTCODE = null;

    private SmsUtils(){}
}
