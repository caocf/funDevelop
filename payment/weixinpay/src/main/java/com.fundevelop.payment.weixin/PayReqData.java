package com.fundevelop.payment.weixin;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 请求调起支付API需要提交的数据.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/14 2:43
 */
class PayReqData implements ReqData{
    /** 应用ID:必填 String(32)	wxd678efh567hg6787	微信开放平台审核通过的应用APPID */
    private String appid;
    /** 商户号:必填 String(32)	1230000109	微信支付分配的商户号 */
    private String partnerid;
    /** 预支付交易会话ID:必填 String(32)WX1217752501201407033233368018 微信返回的支付交易会话ID */
    private String prepayid;
    /** 随机字符串:必填 String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法 */
    private String noncestr = RandomStringGenerator.getRandomStringByLength(32);
    private String timestamp = new Date().getTime()/1000+"";
    /** 签名:必填 String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法 */
    private String sign;

    public PayReqData(String appid, String partnerid, String prepayid) {
        this.appid = appid;
        this.partnerid = partnerid;
        this.prepayid = prepayid;
    }

    @Override
    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public SortedMap toMap() {
        SortedMap map = new TreeMap();
        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);

                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                LoggerFactory.getLogger(getClass()).warn("将参数转换成Map发生异常", e);
            } catch (IllegalAccessException e) {
                LoggerFactory.getLogger(getClass()).warn("将参数转换成Map发生异常", e);
            }
        }

        /** 扩展字段:必填  String(128)	Sign=WXPay	暂填写固定值Sign=WXPay */
        map.put("package", "Sign=WXPay");

        return map;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }
}
