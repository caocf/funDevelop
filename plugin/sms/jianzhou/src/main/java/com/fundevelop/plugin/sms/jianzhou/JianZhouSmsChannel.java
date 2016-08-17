package com.fundevelop.plugin.sms.jianzhou;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.plugin.sms.Sms;
import com.fundevelop.plugin.sms.SmsEventNotifyHandle;
import com.fundevelop.plugin.sms.impl.AbstractSmsChannel;
import com.jianzhou.sdk.BusinessService;
import org.apache.commons.lang3.StringUtils;

/**
 * 建周科技短信通道实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 10:40
 */
public class JianZhouSmsChannel extends AbstractSmsChannel {
    @Override
    public boolean sendSms(Sms sms) {
        int sendRes = 0;
        String phones = null;
        String content = null;

        try {
            phones = linkPhones(sms.getPhones(), ";");
            content = signContent(sms.getContent());

            if (StringUtils.isNotBlank(phones) && StringUtils.isNotBlank(content)) {
                sendRes = getClient().sendBatchMessage(getAccount(), getPassword(), phones, content);

                logger.debug("使用建周科技发送短信：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);

                if (sendRes > 0) {
                    SmsEventNotifyHandle.sendSucess(sms.getSmsId(), getRrid(), getChannelCode());

                    return true;
                } else {
                    SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "建周科技返回数据为空");
                    logger.error("使用建周科技发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                }
            } else {
                SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "没有手机号或内容");
            }
        } catch (Exception ex) {
            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "发生异常："+ex.getMessage());
            logger.error("使用建周科技发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes, ex);
        }

        return false;
    }

    private BusinessService getClient() {
        if (client == null) {
            synchronized (JianZhouSmsChannel.class) {
                if (client == null) {
                    client = new BusinessService();
                    client.setWebService(PropertyUtil.get("jianzhou.sms.webservice.host"));
                }
            }
        }

        return client;
    }

    private BusinessService client;
}
