package com.fundevelop.payment.weixin;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 请求统一下单API需要提交的数据.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 21:12
 */
class PrePayReqData implements ReqData{
    /** 应用ID:必填 String(32)	wxd678efh567hg6787	微信开放平台审核通过的应用APPID */
    private String appid;
    /** 商户号:必填 String(32)	1230000109	微信支付分配的商户号 */
    private String mch_id;
    /** 设备号:非必填 String(32)	013467007045764	终端设备号(门店号或收银设备ID)，默认请传"WEB */
    private String device_info;
    /** 随机字符串:必填 String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法 */
    private String nonce_str = RandomStringGenerator.getRandomStringByLength(32);
    /** 签名:必填 String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法 */
    private String sign;
    /** 商品描述:必填 String(128)	Ipad mini  16G  白色	商品或支付单简要描述 */
    private String body;
    /** 商品详情:非必填 String(8192)	Ipad mini  16G  白色	商品名称明细列表 */
    private String detail;
    /** 附加数据:非必填 String(127)	深圳分店	附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据 */
    private String attach;
    /** 商户订单号:必填 String(32)	20150806125346	商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号 */
    private String out_trade_no;
    /** 货币类型:非必填 String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型 */
    private String fee_type;
    /** 总金额:必填 Int	888	订单总金额，单位为分，详见支付金额 */
    private int total_fee;
    /** 终端IP:必填 String(16)	123.12.12.123	用户端实际ip */
    private String spbill_create_ip;
    /** 交易起始时间:非必填 String(14)	20091225091010
     * 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则 */
    private String time_start;
    /** 交易结束时间:非必填 String(14)	20091227091010
     订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则
     注意：最短失效时间间隔必须大于5分钟 */
    private String time_expire;
    /** 商品标记:非必填 String(32) WXG	商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠 */
    private String goods_tag;
    /** 通知地址:必填 String(256) 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。 */
    private String notify_url;
    /** 交易类型:必填 String(16)	APP	支付类型 */
    private String trade_type = "APP";
    /** 指定支付方式:非必填  String(32)	no_credit	no_credit--指定不能使用信用卡支付 */
    private String limit_pay;

    public PrePayReqData(String appid, String mch_id, String body, String out_trade_no, int total_fee, String spbill_create_ip, String notify_url) {
        this.appid = appid;
        this.mch_id = mch_id;
        this.body = body;
        this.out_trade_no = out_trade_no;
        this.total_fee = total_fee;
        this.spbill_create_ip = spbill_create_ip;
        this.notify_url = notify_url;
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

        return map;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public void setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
    }
}
