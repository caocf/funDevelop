package com.fundevelop.plugin.sms.ihuyi;

import com.fundevelop.commons.web.utils.HttpUtils;
import com.fundevelop.plugin.sms.Phone;
import com.fundevelop.plugin.sms.Sms;
import com.fundevelop.plugin.sms.SmsEventNotifyHandle;
import com.fundevelop.plugin.sms.impl.AbstractSmsChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 互亿无线短信通道实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/9 15:01
 */
public class IhuyiChannel extends AbstractSmsChannel {
    @Override
    public boolean sendSms(Sms sms) {
        if (StringUtils.isBlank(hostUrl)) {
            throw new RuntimeException("没有设置互亿无线发送短信请求地址");
        }

        String sendRes = null;
        String phone = null;
        String content = null;

        try {
            content = signContent(sms.getContent());

            if (sms.getPhones() != null && !sms.getPhones().isEmpty() && StringUtils.isNotBlank(content)) {
                if (sms.getPhones().size() > 1) {
                    throw new RuntimeException("互亿无线接口一次只能发送一个手机号");
                }

                phone = sms.getPhones().get(0).toString();
                Map<String, String> headers = new HashMap<>(1);
                headers.put("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

                if (StringUtils.isNotBlank(phone)) {
                    List<NameValuePair> params = new ArrayList<>(4);
                    params.add(new BasicNameValuePair("account", getAccount()));
                    params.add(new BasicNameValuePair("password", getPassword()));
                    params.add(new BasicNameValuePair("mobile", phone));
                    params.add(new BasicNameValuePair("content", content));

                    sendRes = HttpUtils.executePost(hostUrl, new UrlEncodedFormEntity(params, "UTF-8"), headers);

                    logger.debug("使用互亿无线发送短信：手机号：{}，短信内容：{}，返回结果：{}", phone, content, sendRes);

                    if (StringUtils.isNotBlank(sendRes)) {
                        Document doc = DocumentHelper.parseText(sendRes);

                        Element root = doc.getRootElement();

                        String code = root.elementText("code");
                        String smsid = root.elementText("smsid");

                        if (!"2".equals(code)) {
                            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "互亿无线返回状态为失败，通道返回内容为：" + sendRes);
                            logger.warn("使用互亿无线发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phone, content, sendRes);
                            return  false;
                        }

                        SmsEventNotifyHandle.sendSucess(sms.getSmsId(), smsid, getChannelCode());

                        return true;
                    } else {
                        SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "互亿无线返回数据为空");
                        logger.error("使用互亿无线发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phone, content, sendRes);
                    }
                }
            } else {
                SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "没有手机号或内容");
            }
        } catch (Exception ex) {
            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "发生异常："+ex.getMessage());
            logger.error("使用互亿无线发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phone, content, sendRes, ex);
        }

        return false;
    }

    /** 服务地址 */
    private String hostUrl = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";

    /**
     * 设置发送短信POST服务地址.
     */
    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }
}
