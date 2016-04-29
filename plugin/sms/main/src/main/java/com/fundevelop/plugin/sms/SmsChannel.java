package com.fundevelop.plugin.sms;

import java.util.List;

/**
 * 短信通道接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:38
 */
public interface SmsChannel {
    /**
     * 发送短信.
     */
    public boolean sendSms(Sms sms);

    /**
     * 获取上行短信.
     */
    public List<ReceiveSms> getSms();

    /**
     * 获取短信发送报告（回执）.
     */
    public List<SmsReport> getSendReport();

    /**
     * 获取短信通道代码.
     */
    public String getChannelCode();
}
