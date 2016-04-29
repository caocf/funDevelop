package com.fundevelop.plugin.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信事件通知类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 15:03
 */
public class SmsEventNotifyHandle {
    /**
     * 短信发送成功事件.
     * @param smsId 短信ID
     * @param msgId 通道消息ID
     * @param channelCode 通道代码
     */
    public static void sendSucess(String smsId, String msgId, String channelCode) {
        if (eventListener != null) {
            eventListener.sendSucessEvent(smsId, msgId, channelCode);
        } else {
            logger.debug("短信发送成功，短信ID：{}，短信通道返回ID：{}，短信通道：{}", smsId, msgId, channelCode);
        }
    }

    /**
     * 短信发送失败事件.
     * @param sms 短信
     * @param channelCode 通道代码
     */
    public static void sendFail(Sms sms, String channelCode, String message) {
        if (eventListener != null) {
            eventListener.sendFailEvent(sms,channelCode, message);
        } else {
            logger.warn("短信发送失败，失败原因：{}，短信通道：{}，短信：{}", message, channelCode, sms);
        }
    }

    public static void setEventListener(SmsEventListener eventListener) {
        SmsEventNotifyHandle.eventListener = eventListener;
    }

    /** 短信事件监听器 */
    private static SmsEventListener eventListener;

    private static Logger logger = LoggerFactory.getLogger(SmsEventNotifyHandle.class);

    private SmsEventNotifyHandle(){}
}
