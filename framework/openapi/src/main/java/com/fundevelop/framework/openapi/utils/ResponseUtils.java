package com.fundevelop.framework.openapi.utils;

import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.framework.base.config.ServerConfUtils;
import com.fundevelop.framework.openapi.exception.RestException;
import com.fundevelop.framework.openapi.model.RestError;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        RestError error;

        if (ex instanceof RestException) {
            RestException re = (RestException) ex;
            error = new RestError(ex);
        } else {
            error = new RestError(500, "服务器开小差了~~~");
        }

        response.setError(error);
        response.setStatusCode(error.getErrorCode());

        logger.error("cmd处理出现异常", ex);

        return response;
    }

    /**
     * 成功返回服务处理结果.
     */
    public static RestResponse successReturn(RestRequest restRequest, RestResponse response) {
        if (response == null) {
            return handleException(restRequest, response, null);
        }

        return response;
    }

    private ResponseUtils(){}

    private static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
}
