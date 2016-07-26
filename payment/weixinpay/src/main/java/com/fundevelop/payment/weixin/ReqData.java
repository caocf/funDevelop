package com.fundevelop.payment.weixin;

import java.util.SortedMap;

/**
 * 微信支付请求数据接口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 21:43
 */
interface ReqData {
    void setSign(String sign);

    /**
     * 将对象转为Map.
     */
    SortedMap toMap();
}
