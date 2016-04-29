package com.fundevelop.plugin.sms.impl;

import com.fundevelop.plugin.sms.SmsChannel;
import com.fundevelop.plugin.sms.SmsChannelDispatchRule;

import java.util.List;

/**
 * 轮询策略.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 13:31
 */
public class RollDispatchRule implements SmsChannelDispatchRule {
    @Override
    public String getChannelCode(String system, String module) {
        if (channels != null) {
            return channels.get(getNextIndex()).getChannelCode();
        }

        return null;
    }

    private synchronized int getNextIndex() {
        if (needRoll) {
            curIndex++;

            if (curIndex >= channels.size()) {
                curIndex = 0;
            }
        }

        return curIndex;
    }

    private List<SmsChannel> channels;
    private boolean needRoll = true;
    private int curIndex = 0;

    public void setChannels(List<SmsChannel> channels) {
        this.channels = channels;
        needRoll = (channels.size() > 1);
    }
}
