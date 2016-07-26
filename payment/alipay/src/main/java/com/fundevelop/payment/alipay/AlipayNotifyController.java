package com.fundevelop.payment.alipay;

import com.alipay.util.AlipayNotify;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.payment.constants.PaymentPlatform;
import com.fundevelop.payment.entity.PaymentNotifyRecordEntity;
import com.fundevelop.payment.manager.PaymentNotifyRecordManager;
import com.fundevelop.payment.utils.PaymentUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝支付后回调地址.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/12 16:50
 */
@Controller
@RequestMapping(value = "/common/payment/notify/alipay")
public class AlipayNotifyController {
    /**
     * 处理支付回调.
     */
    @RequestMapping
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        String returnContent = "success";
        PaymentNotifyRecordEntity recordEntity = null;

        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            @SuppressWarnings("rawtypes")
            Map requestParams = request.getParameterMap();
            for (@SuppressWarnings("rawtypes")
                 Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }

            logger.debug("支付宝异步通知参数列表：{}", params);

            //商户订单号get
            String out_trade_no = params.get("out_trade_no");
            //支付宝交易号
            String trade_no = params.get("trade_no");
            //交易状态
            String trade_status = params.get("trade_status");
            // 通知的类型
            String notify_type = params.get("notify_type");
            // 交易金额
            String total_fee = params.get("total_fee");
            // 订单关闭时间
            String gmt_close = params.get("gmt_close");

            // 记录通知报文
            recordEntity = recordNotify(params, out_trade_no, trade_no, trade_status);
            boolean verify = AlipayNotify.verify(params);
            logger.debug("交易号 {} 的校验结果：{}", trade_no, verify);

            if (verify) {//验证成功
                try {
                    if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
                        String refund_status = request.getParameter("refund_status");
                        if (StringUtils.isNotBlank(refund_status) && "REFUND_SUCCESS".equals(refund_status)) {
                            // 支付宝退款回调，不做任何处理
                            logger.warn("支付宝支付完成后接收到支付宝退款回调，忽略该回调");
                        } else {
                            // 判断是否是支付宝的关闭订单回调
                            if (StringUtils.isBlank(gmt_close)) {
                                PaymentUtils.completePay(PaymentPlatform.支付宝, out_trade_no, Integer.parseInt(total_fee, 10)*100, trade_no);
                            } else {
                                logger.warn("支付宝支付完成后接收到支付宝的关闭订单回调，忽略该回调");
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("支付宝支付完成后回调业务处理类发生异常，订单号：" + out_trade_no, ex);
                    returnContent = "fail";
                }
            } else {//验证失败
                logger.error("交易号 {} 退款回调处理失败", trade_no);
                returnContent = "fail";
            }

            logger.debug("支付宝回调：out_trade_no={}, trade_no={}, returnContent={}", out_trade_no, trade_no, returnContent);
        } catch (Exception ex) {
            logger.error("处理支付宝支付后回调发生异常", ex);
            returnContent = "fail";
        } finally {
            if (recordEntity != null) {
                recordEntity.setReturnContent(returnContent);
                notifyRecordEntityManager.save(recordEntity);
            }
        }

        try {
            response.getWriter().println(returnContent);
        } catch (IOException e) {
            logger.error("接收到支付宝支付后回调后回写数据失败", e);
        }
    }

    /**
     * 记录通知日志.
     */
    private PaymentNotifyRecordEntity recordNotify(Map<String, String> params, String orderNo, String platformOrderNo, String trade_status) throws JsonProcessingException {
        return notifyRecordEntityManager.recordNotify(orderNo, platformOrderNo, PaymentPlatform.支付宝.getCode()+"", trade_status, BeanUtils.toJson(params));
    }

    @Autowired
    private PaymentNotifyRecordManager notifyRecordEntityManager;

    /** Log4j日志记录. */
    private Logger logger = LoggerFactory.getLogger(getClass());
}
