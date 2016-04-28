package com.fundevelop.plugin.sms;

import java.util.List;
import java.util.Map;

/**
 * 短信通道接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/28 22:38
 */
public interface SmsChannel {
    /**
     * 发送短信.
     */
    public boolean send(Sms sms);

    /**
     * 获取上行短信.
     */
    public List<Map<String, String>> receive();

    /**
     * 获取上行短信.
     */
    public List<Map<String, String>> getSendReceipt();
}
