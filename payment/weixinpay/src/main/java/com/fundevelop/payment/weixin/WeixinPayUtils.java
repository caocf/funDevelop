package com.fundevelop.payment.weixin;

import com.tenpay.util.XMLUtil;
import org.apache.commons.lang3.StringUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 微信支付工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/13 17:38
 */
public class WeixinPayUtils {
    /**
     * 解析XML内容
     */
    public static SortedMap doParse(String xmlContent) throws JDOMException, IOException {
        //解析xml,得到map
        Map m = XMLUtil.doXMLParse(xmlContent);
        SortedMap parameters = new TreeMap();

        //设置参数
        Iterator it = m.keySet().iterator();
        while(it.hasNext()) {
            String k = (String) it.next();
            String v = (String) m.get(k);

            if (null != v) {
                v = v.trim();
            }

            parameters.put(k, v);
        }

        return parameters;
    }

    public static String getNotifyResponse(String code, String msg) {
        String xml = "<xml>\n" +
                "  <return_code><![CDATA["+code+"]]></return_code>\n" +
                "  <return_msg><![CDATA["+msg+"]]></return_msg>\n" +
                "</xml>\n";

        return xml;
    }

    /**
     * 验证签名.
     * @throws IllegalAccessException
     */
    public static boolean checkSign(Map<String, String> requestParameterMap) throws IllegalAccessException {
        SortedMap parameters = new TreeMap();
        String tenpaySign = null;

        Iterator it = requestParameterMap.keySet().iterator();
        while (it.hasNext()) {
            String k = (String) it.next();
            String v = requestParameterMap.get(k);

            if ( null != v) {
                v = v.trim();
            }

            if ("sign".equals(k)) {
                tenpaySign = v;
            } else if (StringUtils.isNotBlank(v)) {
                parameters.put(k, v);
            }
        }

        String sign = Signature.getSign(parameters);

        return sign.toLowerCase().equals(tenpaySign.toLowerCase());
    }

    private WeixinPayUtils(){}
}
