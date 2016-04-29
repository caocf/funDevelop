package com.fundevelop.plugin.sms.ctcsmscloud;

import com.alibaba.druid.support.json.JSONUtils;
import com.ctc.smscloud.json.JSONHttpClient;
import com.fundevelop.plugin.sms.ReceiveSms;
import com.fundevelop.plugin.sms.Sms;
import com.fundevelop.plugin.sms.SmsEventNotifyHandle;
import com.fundevelop.plugin.sms.SmsReport;
import com.fundevelop.plugin.sms.impl.AbstractSmsChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 大汉三通短信通道实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 14:06
 */
public class CtcSmscloudChannel extends AbstractSmsChannel {
    @Override
    public boolean sendSms(Sms sms) {
        String sendRes = null;
        String phones = null;
        String content = null;

        try {
            phones = linkPhones(sms.getPhones(), ",");
            content = signContent(sms.getContent());

            if (StringUtils.isNotBlank(phones) && StringUtils.isNotBlank(content)) {
                sendRes = client.sendSms(getAccount(), getPassword(), phones, content, getSign(), subcode);

                logger.debug("使用大汉三通发送短信：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);

                if (StringUtils.isNotBlank(sendRes)) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> result = (Map<String, String>) JSONUtils.parse(sendRes);
                    String status = result.get("result");
                    String msgId = result.get("msgid");

                    if (!"0".equals(status)) {
                        SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "大汉三通返回状态为失败，通道返回内容为：" + sendRes);
                        logger.warn("使用大汉三通发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                        return false;
                    }

                    if (StringUtils.isNotBlank(result.get("failPhones"))) {
                        logger.warn("使用大汉三通发送短信部分失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                    }

                    SmsEventNotifyHandle.sendSucess(sms.getSmsId(), msgId, getChannelCode());

                    return true;
                } else {
                    SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "大汉三通返回数据为空");
                    logger.error("使用大汉三通发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes);
                }
            } else {
                SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "没有手机号或内容");
            }
        } catch (Exception ex) {
            SmsEventNotifyHandle.sendFail(sms, getChannelCode(), "发生异常："+ex.getMessage());
            logger.error("使用大汉三通发送短信失败：手机号：{}，短信内容：{}，返回结果：{}", phones, content, sendRes, ex);
        }

        return false;
    }

    @Override
    public List<ReceiveSms> getSms() {
        try {
            String smsRes = client.getSms(getAccount(), getPassword());

            logger.debug("使用大汉三通获取上行短信：返回结果：{}", smsRes);

            if (StringUtils.isNotBlank(smsRes)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>)JSONUtils.parse(smsRes);
                String status = result.get("result").toString();

                if ("0".equals(status)) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> delivers = (List<Map<String, String>>)result.get("delivers");

                    if (delivers != null && !delivers.isEmpty()) {
                        List<ReceiveSms> deliverSmsList = new ArrayList<ReceiveSms>(delivers.size());

                        for (Map<String, String> sms : delivers) {
                            deliverSmsList.add(new ReceiveSms(sms.get("phone"), sms.get("content"), sms.get("delivertime")));
                        }

                        return deliverSmsList;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("使用大汉三通获取上行短信失败", ex);
        }

        return null;
    }

    @Override
    public List<SmsReport> getSendReport() {
        String reportRes = client.getReport(getAccount(), getPassword());
        logger.debug("使用大汉三通获取短信状态报告：{}", reportRes);

        if (StringUtils.isNotBlank(reportRes)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>)JSONUtils.parse(reportRes);

                if ("0".equals(result.get("result"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> reports = (List<Map<String, String>>)result.get("reports");

                    if (reports != null && !reports.isEmpty()) {
                        List<SmsReport> smsReports = new ArrayList<>(reports.size());

                        for (Map<String, String> report : reports) {
                            try {
                                String rrid = report.get("msgid");
                                String serviceCode = report.get("status");
                                String status = report.get("wgcode");
                                String time = report.get("time");

                                if (StringUtils.isNotBlank(rrid)) {
                                    Timestamp responseTime = null;

                                    if (StringUtils.isNotBlank(time)) {
                                        Date d = null;

                                        try {
                                            d = DateUtils.parseDate(time, new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" });
                                        } catch (ParseException e) {
                                            d = null;
                                        }

                                        if (d != null) {
                                            responseTime = new Timestamp(d.getTime());
                                        } else {
                                            responseTime = new Timestamp(System.currentTimeMillis());
                                        }
                                    } else {
                                        responseTime = new Timestamp(System.currentTimeMillis());
                                    }

                                    smsReports.add(new SmsReport(rrid, serviceCode, status, responseTime, getChannelCode()));
                                }
                            } catch (Exception ex) {
                                logger.warn("记录大汉三通短信状态报告失败：报告：{}", report, ex);
                            }
                        }

                        return smsReports;
                    }
                } else {
                    logger.warn("使用大汉三通获取短信状态报告失败：返回结果：{}", reportRes);
                }
            } catch (Exception ex) {
                logger.warn("使用大汉三通获取短信状态报告失败：返回结果：{}", reportRes, ex);
            }
        }

        return null;
    }

    /** 短信发送客户端. */
    private static JSONHttpClient client = JSONHttpClient.getInstance("wt.3tong.net");

    /** 短信签名对应子码. */
    private String subcode;

    /**
     * 设置短信签名对应子码.
     * @date 2016-01-27  17:18:07
     */
    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }
}
