package com.fundevelop.plugin.sms;

import com.fundevelop.plugin.sms.impl.RollDispatchRule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信发送控制类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 12:44
 */
public class SmsSender {
    /**
     * 发送短信.
     */
    public static void sendSms(Sms sms) {
        if (sms != null && sms.getPhones() != null && !sms.getPhones().isEmpty() && StringUtils.isNotBlank(sms.getContent())) {
            SmsChannel channel = getChannel(sms.getSystem(), sms.getModule());

            if (channel != null) {
                channel.sendSms(sms);
            } else {
                SmsEventNotifyHandle.sendFail(sms, null, "无法获取短信通道");
                logger.warn("根据业务系统名称或代码（{}）及业务系统模块名称或代码（{}）无法获取对应的短信通道", sms.getSystem(), sms.getModule());
            }
        } else {
            SmsEventNotifyHandle.sendFail(sms, null, "没有手机号或内容");
        }
    }

    /**
     * 发送短信.
     */
    public static void sendSms(List<Sms> smses) {
        if (smses != null && !smses.isEmpty()) {
            for (Sms sms : smses) {
                sendSms(sms);
            }
        }
    }

    /**
     * 发送短信.
     */
    public static void sendSms(Sms... smses) {
        sendSms(Arrays.asList(smses));
    }

    /**
     * 根据短信通道代码获取短信通道实例.
     */
    public static SmsChannel getChannelByCode(String channelCode) {
        if (StringUtils.isNotBlank(channelCode) && channels != null && channels.containsKey(channelCode)) {
            return channels.get(channelCode);
        }

        return null;
    }

    /**
     * 根据系统及模块代码获取短信发送通道.
     * @param system 业务系统名称或代码
     * @param module 业务系统模块名称或代码
     * @return 对应的短信通道
     */
    private static SmsChannel getChannel(String system, String module) {
        if (channels != null && !channels.isEmpty()) {
            String channelCode = null;

            if (channelDispatchRule != null) {
                channelCode = channelDispatchRule.getChannelCode(system, module);
            }

            if (StringUtils.isBlank(channelCode) && StringUtils.isNotBlank(defaultChannel)) {
                channelCode = defaultChannel;
            }

            if (StringUtils.isNotBlank(channelCode) && channels.containsKey(channelCode)) {
                return channels.get(channelCode);
            }
        }

        return null;
    }

    /**
     * 设置默认通道.
     * @param channelCode 默认短信通道代码
     */
    public void setDefaultChannel(String channelCode) {
        SmsSender.defaultChannel = channelCode;
    }

    /**
     * 设置短信通道.
     */
    public void setChannels(List<SmsChannel> channels) {
        Map<String, SmsChannel> channelMap = new HashMap<>(channels.size());

        for (SmsChannel channel : channels) {
            channelMap.put(channel.getChannelCode(), channel);
        }

        SmsSender.channels = channelMap;

        if (SmsSender.channelDispatchRule != null && SmsSender.channelDispatchRule instanceof RollDispatchRule) {
            ((RollDispatchRule)SmsSender.channelDispatchRule).setChannels(channels);
        }
    }

    /**
     * 设置短信通道分配器.
     */
    public void setChannelDispatchRule(SmsChannelDispatchRule channelDispatchRule) {
        SmsSender.channelDispatchRule = channelDispatchRule;
    }

    /**
     * 设置短信事件监听器.
     */
    public void setEventListener(SmsEventListener eventListener) {
        SmsEventNotifyHandle.setEventListener(eventListener);
    }

    /** 短信通道 */
    private static Map<String, SmsChannel> channels;
    /** 默认短信通道 */
    private static String defaultChannel;
    /** 短信通道分配规则 */
    private static SmsChannelDispatchRule channelDispatchRule = new RollDispatchRule();

    private static Logger logger = LoggerFactory.getLogger(SmsSender.class);
}
