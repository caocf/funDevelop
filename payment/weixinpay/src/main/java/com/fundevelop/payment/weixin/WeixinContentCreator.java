package com.fundevelop.payment.weixin;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.payment.base.PaymentContentCreator;
import com.fundevelop.payment.base.PaymentInfo;
import com.tencent.common.HttpsRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * 微信支付支付信息构造类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/11 22:48
 */
@Component("fun_paymentContentCreator_1")
public class WeixinContentCreator implements PaymentContentCreator {
    @Override
    public Object buildContent(PaymentInfo paymentInfo) {
        try {
            //接收财付通通知的URL
            String notifyUrl = PropertyUtil.get("payment.weixinpay.notifyUrl");
            String appId = PropertyUtil.get("payment.weixinpay.appId");
            String mchId = PropertyUtil.get("payment.weixinpay.mchId");
            String hostUrl = PropertyUtil.get("payment.weixinpay.createOrder.url");
            String tradeType = PropertyUtil.get("payment.weixinpay.tradeType", "APP");

            if (StringUtils.isBlank(notifyUrl)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.weixinpay.notifyUrl属性(微信支付回调地址)");
            }
            if (StringUtils.isBlank(appId)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.weixinpay.appId属性(微信开放平台审核通过的支付应用APPID)");
            }
            if (StringUtils.isBlank(mchId)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.weixinpay.mchId属性(微信支付分配的商户号)");
            }
            if (StringUtils.isBlank(hostUrl)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.weixinpay.createOrder.url属性(微信支付下单接口地址)");
            }
            if (StringUtils.isNotBlank(paymentInfo.getTradeType())) {
                tradeType = paymentInfo.getTradeType();
            }

            PrePayReqData prePayReqData = new PrePayReqData(appId, mchId, paymentInfo.getProductName(), paymentInfo.getOrderNo(), paymentInfo.getOrderAmount(), paymentInfo.getIp(), notifyUrl);
            prePayReqData.setDetail(paymentInfo.getDesc());
            prePayReqData.setTrade_type(tradeType);

            Signature.sign(prePayReqData);

            HttpsRequest httpsRequest = new HttpsRequest();
            String prePayResult = httpsRequest.sendPost(hostUrl, prePayReqData);

            if (StringUtils.isBlank(prePayResult)) {
                logger.error("获取prepayId失败，params: {}, debug[ prepay ]: {}", prePayReqData, prePayResult);
                throw new RuntimeException("获取微信支付信息失败");
            }

            SortedMap resultMap = WeixinPayUtils.doParse(prePayResult);
            String prepayId = (String) resultMap.get("prepay_id");

            if (StringUtils.isBlank(prepayId)) {
                logger.error("获取prepayId失败，params: {}, debug[ prepay ]: {}", prePayReqData, resultMap);
                throw new RuntimeException("获取微信支付信息失败");
            }

            PayReqData payReqData = new PayReqData(appId, mchId, prepayId);
            payReqData.setNoncestr(prePayReqData.getNonce_str());

            Signature.sign(payReqData);

            Map<String, Object> result = new HashMap<>(1);
            Map<String, Object> data = new HashMap<>(2);

            data.put("data",Signature.toString(payReqData.toMap()));
            data.put("qrCodeUrl",resultMap.get("code_url"));

            result.put("weiXinPay",data);

            return result;
        } catch (Exception ex) {
            logger.error("构造微信支付信息时发生异常！", ex);
            throw new RuntimeException("构造微信支付信息时发生异常", ex);
        }
    }

    /** Log4j日志记录. */
    private Logger logger = LoggerFactory.getLogger(getClass());
}
