package com.fundevelop.payment.alipay;

import com.alipay.util.MD5;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.payment.base.PaymentContentCreator;
import com.fundevelop.payment.base.PaymentInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * 支付宝支付信息构造类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/12 14:37
 */
@Component("fun_paymentContentCreator_2")
public class AlipayContentCreator implements PaymentContentCreator {
    @Override
    public Object buildContent(PaymentInfo paymentInfo) {
        try {
            String notifyUrl = PropertyUtil.get("payment.alipay.notifyUrl");
            String partner = PropertyUtil.get("payment.alipay.partner");
            String sellerId = PropertyUtil.get("payment.alipay.sellerId");
            String signKey = PropertyUtil.get("payment.alipay.sign.key");

            if (StringUtils.isBlank(notifyUrl)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.alipay.notifyUrl属性(支付宝支付回调地址)");
            }
            if (StringUtils.isBlank(partner)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.alipay.partner属性(支付宝支付合作者身份ID)");
            }
            if (StringUtils.isBlank(sellerId)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.alipay.sellerId属性(支付宝支付卖家支付宝用户号)");
            }
            if (StringUtils.isBlank(signKey)) {
                throw new RuntimeException("请在application.properties配置文件中配置payment.alipay.sign.key属性(支付宝支付数据签名Key)");
            }


            String returnUrl = PropertyUtil.get("payment.alipay.returnUrl");

            StringBuilder alipayText = new StringBuilder();
            alipayText.append("partner=").append(partner); // 商家名
            alipayText.append("&seller_id=").append(sellerId); // 支付宝账号
            alipayText.append("&out_trade_no=").append(paymentInfo.getOrderNo()); // 订单号
            alipayText.append("&subject="); // 商品名
            alipayText.append(paymentInfo.getProductName());

            alipayText.append("&body="); // 商品描述
            alipayText.append(StringUtils.defaultString(paymentInfo.getDesc(),""));

            alipayText.append("&total_fee=").append((paymentInfo.getOrderAmount()/100.00)); // 支付金额
            alipayText.append("&notify_url=").append(URLEncoder.encode(notifyUrl,"UTF-8")); // 支付完成回调url

            if (StringUtils.isNotBlank(returnUrl)) {
                alipayText.append("&return_url=");
                alipayText.append(URLEncoder.encode(returnUrl, "UTF-8"));
            }

            alipayText.append("&service=").append(PropertyUtil.get("payment.alipay.service")); // 默认填写，@"mobile.securitypay.pay"
            alipayText.append("&_input_charset=UTF-8"); // 默认填写，@"utf-8"
            alipayText.append("&payment_type=1"); // 默认填写，@"1"
            alipayText.append("&it_b_pay=1m"); // 超时时间

            //签名
            String sign = MD5.sign(alipayText.toString(), signKey, "utf-8");
            sign = URLEncoder.encode(sign, "UTF-8");

            String message = alipayText.toString() + "&sign=" + sign + "&sign_type=MD5";

            logger.debug("订单 {} 的[支付宝]支付明文\n{}", paymentInfo.getOrderNo(), alipayText);
            logger.debug("订单 {} 的[支付宝]加密报文\n{}", paymentInfo.getOrderNo(), message);

            return message;
        } catch (Exception ex) {
            logger.error("构造支付宝支付信息时发生异常！", ex);
            throw new RuntimeException("构造支付宝支付信息时发生异常", ex);
        }
    }

    /** Log4j日志记录. */
    private Logger logger = LoggerFactory.getLogger(getClass());
}
