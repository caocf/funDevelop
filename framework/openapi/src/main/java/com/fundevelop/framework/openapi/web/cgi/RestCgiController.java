package com.fundevelop.framework.openapi.web.cgi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.UuidGenerator;
import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.commons.web.utils.MediaTypes;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.config.ServerConfUtils;
import com.fundevelop.framework.openapi.exception.RestException;
import com.fundevelop.framework.openapi.model.BaseRestRequest;
import com.fundevelop.framework.openapi.model.RestError;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import com.fundevelop.framework.openapi.utils.CmdServiceInvoker;
import com.fundevelop.framework.openapi.utils.ResponseUtils;
import com.fundevelop.framework.openapi.utils.RestCmdHelps;
import com.fundevelop.framework.openapi.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

/**
 * Restful cgi openapi总入口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 17:55
 */
@RestController
@RequestMapping(value = "/cgi")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestCgiController extends BaseController {
    /**
     * 初始化CGI.
     */
    public RestCgiController() {
        logger.trace("准备开始初始化CGI服务...");


        logger.trace("CGI服务初始化完毕.");
    }

    /**
     * GET请求，支持JSONP.
     */
    @RequestMapping(method = RequestMethod.GET)
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        RestResponse restResponse = null;
        RestRequest restRequest = null;

        try {
            restRequest = initRequest(request);

            if (restRequest == null) {
                throw new RuntimeException("从Get请求中获取请求参数失败");
            }

            restResponse = handleRequest(restRequest, request);
        } catch (Exception ex) {
            restResponse = ResponseUtils.handleException(restRequest, restResponse, ex);
        }

        try {
            ResponseUtils.printJsonForJsonp(response, restRequest, restResponse);
        } catch (IOException e) {
            logger.error("使用Get请求将结果转换为jsonp输出失败", e);
        }
    }

    /**
     * 处理POST请求.
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
    public RestResponse handleRequest(@RequestBody final RestRequest restRequest, HttpServletRequest httpRequest) {
        RestResponse response = null;

        try {
            initRequest(restRequest, httpRequest);
            response = ResponseUtils.createByRequest(restRequest);

            String cmd = restRequest.getCmd();

            // 记录请求日志
            logRequest(restRequest, httpRequest);

            // 处理命令异常
            if (StringUtils.isBlank(cmd)) {
                RestError error = new RestError(412, "请求的命令不能为空");
                response.setError(error);
                return response;
            }

            BaseRestRequest brestRequest = new BaseRestRequest();
            brestRequest.setHttpRequest(httpRequest);
            brestRequest.setRestRequest(restRequest);

            CmdServiceInvoker invoker = RestCmdHelps.getInvoker(brestRequest);

            // 拦截未知cmd
            if (invoker == null) {
                RestError error = new RestError(410, "cmd not found in [" + ServerConfUtils.getConf().getServiceName() + "]");
                response.setError(error);
                return response;
            }

            invoker.invoke(restRequest, response);

            response.setClientIp(IpUtils.getIpAddr(httpRequest));
        } catch (RestException re) {
            return ResponseUtils.handleException(restRequest, response, re);
        } catch (Exception ex) {
            return ResponseUtils.handleException(restRequest, response, ex);
        } catch (Throwable throwable) {
            return ResponseUtils.handleException(restRequest, response, throwable);
        }

        return ResponseUtils.successReturn(restRequest, response);
    }

    private void logRequest(RestRequest request, HttpServletRequest httpRequest) {
        try {
            String threadName = Thread.currentThread().toString();
            String token = request.getToken();
            String clientIp = IpUtils.getIpAddr(httpRequest);
            String cgiServiceName = ServerConfUtils.getConf().getServiceName();
            if (logger.isDebugEnabled()) {
                logger.debug("RECEIVE_REQUEST, cgi: {}, ip:{}, thread:{}, token: {}, content:\n{}",
                        cgiServiceName, clientIp, threadName, token, BeanUtils.toJson(request));
            } else {
                logger.info("RECEIVE_REQUEST, cgi: {}, ip:{}, thread:{}, token: {}, content: \n{}",
                        cgiServiceName, clientIp, threadName, token, BeanUtils.toJson(request));
            }
        } catch (Exception ex) {
            logger.warn("记录CGI请求日志出现异常", ex);
        }
    }

    /**
     * 初始化请求.
     */
    private RestRequest initRequest(HttpServletRequest request) {
        RestRequest restRequest = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            ObjectNode parameters = mapper.createObjectNode();

            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String key : parameterMap.keySet()) {
                String[] values = parameterMap.get(key);

                if (StringUtils.equals("_", key)) {
                    continue;
                }

                // 特殊处理parameters
                if (StringUtils.startsWith(key, "parameters")) {
                    String newKey = key.replaceAll("parameters\\[", "").replaceAll("]", "");
                    parameters.put(newKey, URLDecoder.decode(values[0], "UTF-8"));
                    continue;
                }

                // 其他参数
                if (values.length > 1) {
                    ArrayNode arrayNode = mapper.createArrayNode();
                    for (int i = 0; i < values.length; i++) {
                        arrayNode.add(values[i]);
                    }
                    objectNode.set(key, arrayNode);
                } else {
                    String stringValue = URLDecoder.decode(values[0], "UTF-8");
                    objectNode.put(key, stringValue);
                }
            }

            objectNode.set("parameters", parameters);

            restRequest = mapper.convertValue(objectNode, RestRequest.class);
        } catch (Exception ex) {
            logger.error("从Get请求中获取参数出错", ex);
            throw new RuntimeException("从Get请求中获取参数出错", ex);
        }

        return restRequest;
    }
}
