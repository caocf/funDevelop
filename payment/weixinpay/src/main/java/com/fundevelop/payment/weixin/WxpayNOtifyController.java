package com.fundevelop.payment.weixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.payment.constants.PaymentPlatform;
import com.fundevelop.payment.entity.PaymentNotifyRecordEntity;
import com.fundevelop.payment.manager.PaymentNotifyRecordManager;
import com.fundevelop.payment.utils.PaymentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 微信支付后回调入口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/8 15:24
 */
@Controller
@RequestMapping(value = "/common/payment/notify/wxpay")
public class WxpayNOtifyController {
    /**
     * 处理支付回调.
     */
    @RequestMapping
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        PaymentNotifyRecordEntity recordEntity = null;
        String retContent = null;

        try {
            // 获取微信POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map requestParams = request.getParameterMap();

            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }

                params.put(name, valueStr);
            }

            InputStream in = null;
            ByteArrayOutputStream out = null;

            try {
                in = request.getInputStream();
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;

                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                params.putAll(WeixinPayUtils.doParse(new String(out.toByteArray(), "UTF-8")));
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception ex) {
                }
            }

            logger.info("微信支付异步通知参数列表：{}", params);

            String out_trade_no = params.get("out_trade_no");
            String total_fee = params.get("total_fee");
            //        String time_end = params.get("time_end");
            String transaction_id = params.get("transaction_id");

            recordEntity = recordNotify(params, out_trade_no, transaction_id, "completePay");

            if ("SUCCESS".equals(params.get("return_code"))) {
                if (WeixinPayUtils.checkSign(params)) {
                    PaymentUtils.completePay(PaymentPlatform.微信, out_trade_no, Integer.parseInt(total_fee, 10), transaction_id);

                    response.getWriter().write(WeixinPayUtils.getNotifyResponse("SUCCESS", "OK"));
                    retContent = "SUCCESS";
                } else {
                    logger.warn("微信支付通知签名验证失败, params={}", params);
                    response.getWriter().write(WeixinPayUtils.getNotifyResponse("FAIL", "签名验证失败"));
                    retContent = "FAIL  签名验证失败";
                }
            } else {
                logger.warn("微信支付通知状态为失败, params={}", params);
                response.getWriter().write(WeixinPayUtils.getNotifyResponse("FAIL", params.get("return_msg")));
                retContent = "FAIL  " + params.get("return_msg");
            }
        } catch (Exception ex) {
            logger.error("处理微信支付后回调发生异常", ex);
            try {
                response.getWriter().write(WeixinPayUtils.getNotifyResponse("FAIL", "处理出错"));
                retContent = "FAIL  处理出错";
            } catch (Exception e) {}
        } finally {
            if (recordEntity != null) {
                recordEntity.setReturnContent(retContent);
                notifyRecordEntityManager.save(recordEntity);
            }
        }
    }

    /**
     * 记录通知日志.
     */
    private PaymentNotifyRecordEntity recordNotify(Map<String, String> params, String orderNo, String platformOrderNo, String trade_status) throws JsonProcessingException {
        return notifyRecordEntityManager.recordNotify(orderNo, platformOrderNo, PaymentPlatform.微信.getCode()+"", trade_status, BeanUtils.toJson(params));
    }

    @Autowired
    private PaymentNotifyRecordManager notifyRecordEntityManager;

    /** Log4j日志记录. */
    private Logger logger = LoggerFactory.getLogger(getClass());
}
