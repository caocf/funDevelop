package com.fundevelop.plugin.sms;

/**
 * 短信通道分配规则接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 9:37
 */
public interface SmsChannelDispatchRule {
    /**
     * 根据系统及模块代码获取短信发送通道.
     * @param system 业务系统名称或代码
     * @param module 业务系统模块名称或代码
     * @return 对应的短信通道
     */
    String getChannelCode(String system, String module);
}
