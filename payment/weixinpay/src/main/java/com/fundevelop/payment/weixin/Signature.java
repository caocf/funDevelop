package com.fundevelop.payment.weixin;

import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 签名工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 21:28
 */
class Signature {
    /**
     * 签名算法.
     * @param data 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static void sign(ReqData data) throws IllegalAccessException {
        data.setSign(getSign(data.toMap()));
    }

    /**
     * 将对象转换为URL参数字符串.
     * @throws IllegalAccessException
     */
    public static String toString(ReqData data) throws IllegalAccessException {
        return toString(data.toMap());
    }

    /**
     * 将对象转换为URL参数字符串.
     * @throws IllegalAccessException
     */
    public static String toString(SortedMap parameters) throws IllegalAccessException {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            sb.append(k + "=" + v + "&");
        }

        return sb.substring(0, sb.lastIndexOf("&"));
    }

    /**
     * 签名算法.
     * @param parameters 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(SortedMap parameters) throws IllegalAccessException {
        String signKey = PropertyUtil.get("payment.weixinpay.sign.key");

        if (StringUtils.isBlank(signKey)) {
            throw new RuntimeException("请在application.properties配置文件中配置payment.weixinpay.sign.key属性(微信支付数据签名Key)");
        }

        String result = toString(parameters);
        result += "&key=" + signKey;
        logger.debug("Sign Before MD5:" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        logger.debug("Sign Result:" + result);

        return result;
    }

    private static Logger logger = LoggerFactory.getLogger(Signature.class);
}
