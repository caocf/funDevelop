package com.fundevelop.framework.openapi.utils;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.StringUtils;
import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.config.ServerConfUtils;
import com.fundevelop.framework.openapi.exception.RestException;
import com.fundevelop.framework.openapi.model.RestError;
import com.fundevelop.framework.openapi.model.RestErrorV2;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 响应工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 20:43
 */
public class ResponseUtils {
    /**
     * 通过RestRequest创建RestResponse.
     */
    public static RestResponse createByRequest(RestRequest request) {
        RestResponse restResponse = new RestResponse();
        restResponse.setRequestId(request.getRequestId());
        restResponse.setCmd(request.getCmd());
        restResponse.setMessageId(request.getMessageId());

        // 设置处理请求的服务器名称
        if (request.isDebug()) {
            restResponse.getExtraInfo().put("serverIp", IpUtils.getServerIp());

            try {
                restResponse.getExtraInfo().put("serverName", InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                logger.error("get hostname fail:", e);
            }
        }

        return restResponse;
    }

    /**
     * 处理异常返回.
     */
    public static RestResponse handleException(RestRequest restRequest, RestResponse response, Throwable ex) {
        if (restRequest.isDebug() && !restRequest.getReplaceParams().isEmpty()) {
            response.setResponse(restRequest.getReplaceParams());

            logger.warn("cmd处理出现异常，启用调试模式直接返回前端构造数据", ex);
        } else {
            RestError error;

            boolean useV2 = StringUtils.isBooleanTrue(PropertyUtil.get("fun.openapi.restError.useV2", "false"));

            if (ex instanceof RestException) {
                if (useV2) {
                    response.setStatusCode(((RestException) ex).getErrorCode());
                    error = new RestErrorV2((RestException) ex);
                } else {
                    error = new RestError((RestException) ex);
                    response.setStatusCode(error.getErrorCode());
                }
            } else {
                if (useV2) {
                    error = new RestErrorV2(500, "服务器开小差了~~~");
                } else {
                    error = new RestError(500, "服务器开小差了~~~");
                }

                response.setStatusCode(error.getErrorCode());
            }

            response.setError(error);

            logger.error("cmd处理出现异常", ex);
        }

        logResponse(restRequest, response);

        return response;
    }

    /**
     * 成功返回服务处理结果.
     */
    public static RestResponse successReturn(RestRequest restRequest, RestResponse response) {
        if (response == null) {
            return handleException(restRequest, restRequest!=null?createByRequest(restRequest):new RestResponse(), null);
        }

        logResponse(restRequest, response);

        return response;
    }

    /**
     * 打印支持JSONP格式的结果.
     * @throws java.io.IOException
     */
    public static void printJsonForJsonp(HttpServletResponse response, RestRequest restRequest, RestResponse restResponse) throws IOException {
        PrintWriter out = response.getWriter();
        String jsonString = BeanUtils.toJson(restResponse);
        response.setContentType("text/javascript; charset=UTF-8");
        out.print(restRequest.getCallback() + "(" + jsonString + ")");
    }

    private static void logResponse(RestRequest request, RestResponse response) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("REST_RESP, cmd: {}, thread: {}, requestId: {}, response: {}\n{}",
                        request.getCmd(), Thread.currentThread().getName(), response.getRequestId(), BeanUtils.toJson(response));
            } else {
                logger.info("REST_RESP, cmd: {}, thread: {}, requestId: {}, response: {}",
                        request.getCmd(), Thread.currentThread().getName(), response.getRequestId(), BeanUtils.toJson(response));
            }
        } catch (Exception ex) {
            logger.warn("记录CGI响应日志出现异常", ex);
        }
    }

    private ResponseUtils(){}

    private static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
}
