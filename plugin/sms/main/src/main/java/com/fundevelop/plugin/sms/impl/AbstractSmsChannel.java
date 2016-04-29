package com.fundevelop.plugin.sms.impl;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.plugin.sms.Phone;
import com.fundevelop.plugin.sms.ReceiveSms;
import com.fundevelop.plugin.sms.SmsChannel;
import com.fundevelop.plugin.sms.SmsReport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 短信通道默认实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 8:54
 */
public abstract class AbstractSmsChannel implements SmsChannel {
    /** 不需要短信签名 */
    protected final static String CONTENT_SIGN_POSTION_WITHOUT_ = "0";
    /** 短信签名放置在内容的开头 */
    protected final static String CONTENT_SIGN_POSTION_FRONT = "1";
    /** 短信签名放置在内容的末尾 */
    protected final static String CONTENT_SIGN_POSTION_END = "2";
    /** 短信签名放置在内容的开头及末尾 */
    protected final static String CONTENT_SIGN_POSTION_BOTH = "3";

    /**
     * 对短信内容进行签名.
     */
    public String signContent(String smsContent) {
        if (StringUtils.isNotBlank(smsContent)) {
            String sign = getSign();

            if (StringUtils.isNotBlank(sign)) {
                smsContent = StringUtils.remove(smsContent, sign);

                String signPostion = getSignPostion();

                if (CONTENT_SIGN_POSTION_FRONT.equals(signPostion)) {
                    smsContent = sign + smsContent;
                } else if (CONTENT_SIGN_POSTION_END.equals(signPostion)) {
                    smsContent = smsContent + sign;
                } else if (CONTENT_SIGN_POSTION_BOTH.equals(signPostion)) {
                    smsContent = sign + smsContent + sign;
                }
            }
        }

        return smsContent;
    }

    /**
     * 将所有手机号通过<code>separator</code>进行连接.
     */
    protected String linkPhones(List<Phone> phoneList, String separator) {
        StringBuilder phones = new StringBuilder();
        boolean isFirst = true;

        for (Phone phone : phoneList) {
            if (phone != null && StringUtils.isNotBlank(phone.getPhone())) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    phones.append(separator);
                }

                if (StringUtils.isNotBlank(phone.getCountryCode())) {
                    phones.append(phone.getCountryCode());
                }

                phones.append(phone.getPhone());
            }
        }

        return phones.toString();
    }

    /**
     * 获取短信签名位置.
     */
    protected String getSignPostion() {
        return PropertyUtil.get("sms.content.sign.position."+getChannelCode(), PropertyUtil.get("sms.content.sign.position"));
    }

    /**
     * 获取短信签名.
     */
    protected String getSign() {
        return PropertyUtil.get("sms.content.sign."+getChannelCode(), PropertyUtil.get("sms.content.sign"));
    }

    /**
     * 获取rrid.
     */
    protected String getRrid() {
        return getChannelCode()+"_"+System.currentTimeMillis();
    }

    @Override
    public List<ReceiveSms> getSms() {
        return null;
    }

    @Override
    public List<SmsReport> getSendReport() {
        return null;
    }

    /** 账号. */
    private String account;
    /** 密码. */
    private String password;
    /** 通道代码. */
    private String channelCode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());
}
