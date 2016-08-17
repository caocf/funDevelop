package com.fundevelop.plugin.sms.isms360;

import com.fundevelop.plugin.sms.Phone;
import com.fundevelop.plugin.sms.Sms;
import com.fundevelop.plugin.sms.SmsEventNotifyHandle;
import com.fundevelop.plugin.sms.SmsReport;
import com.fundevelop.plugin.sms.impl.AbstractSmsChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 国际短信短信通道实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/8/17 13:02
 */
public class IsmsmtChannel extends AbstractSmsChannel {
    @Override
    public boolean sendSms(Sms sms) {
        String sendRes = null;
        String phones = null;
        String content = null;

        try {
            for (Phone phone : sms.getPhones()) {
                if (StringUtils.isBlank(phone.getCountryCode())) {
                    phone.setCountryCode("86");
                }
            }

            phones = linkPhones(sms.getPhones(), ",");
            content = signContent(sms.getContent());

            if (StringUtils.isNotBlank(phones) && StringUtils.isNotBlank(content)) {
                String hex = WebNetEncode.encodeHexStr(8, content);
                hex = hex.trim() + "&codec=8";

                sendRes = client.sendPostMessage(getAccount(), getPassword(), SEND_SERVICEID, phones, "", hex);

                logger.debug("使用国际短信发送短信：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);

                if (StringUtils.isNotBlank(sendRes)) {
                    String[] res = sendRes.split(",");

                    if (res != null && res.length > 0) {
                        int idx = 0;
                        StringBuffer failPhones = new StringBuffer();
                        StringBuffer msgIds = new StringBuffer();

                        for (;idx<res.length; idx++) {
                            if (res[idx].startsWith("-")) {
                                if (failPhones.length() > 0) {
                                    failPhones.append(",");
                                }

                                failPhones.append(sms.getPhones().get(idx).toString());
                            } else {
                                if (msgIds.length() > 0) {
                                    msgIds.append(",");
                                }

                                msgIds.append(res[idx]);
                            }
                        }

                        if (msgIds.length()==0) {
                            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "国际短信返回状态为失败，通道返回内容为：" + sendRes);
                            logger.warn("使用国际短信发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                            return false;
                        }

                        if (failPhones.length() > 0) {
                            logger.warn("使用国际短信发送短信部分失败：手机号：{}，短信内容：{}，返回结果：{}，失败手机号码：{}", phones, content, sendRes, failPhones.toString());
                        }

                        SmsEventNotifyHandle.sendSucess(sms.getSmsId(), msgIds.toString(), getChannelCode());

                        return true;
                    } else {
                        SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "国际短信返回数据为空");
                        logger.error("使用国际短信发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                    }
                } else {
                    SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "国际短信返回数据为空");
                    logger.error("使用国际短信发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                }
            } else {
                SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "没有手机号或内容");
            }
        } catch (Exception ex) {
            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "发生异常："+ex.getMessage());
            logger.error("使用国际短信发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes, ex);
        }

        return false;
    }

    @Override
    public List<SmsReport> getSendReport() {
        return super.getSendReport();
    }

    /** 短信发送客户端. */
    private static HttpClientUtil client = new HttpClientUtil("210.51.190.233", 8085, "/mt/MT3.ashx");

    private static final String SEND_SERVICEID = "SEND";
}
