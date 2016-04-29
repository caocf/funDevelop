package com.fundevelop.plugin.sms;

/**
 * 短信事件监听器.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 14:51
 */
public interface SmsEventListener {
    /**
     * 短信发送成功事件.
     * @param smsId 短信ID
     * @param msgId 通道消息ID
     * @param channelCode 通道代码
     */
    public void sendSucessEvent(String smsId, String msgId, String channelCode);

    /**
     * 短信发送失败事件.
     * @param sms 短信
     * @param channelCode 通道代码
     */
    public void sendFailEvent(Sms sms, String channelCode, String message);
}
