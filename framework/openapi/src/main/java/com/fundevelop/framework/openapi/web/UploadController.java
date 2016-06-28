package com.fundevelop.framework.openapi.web;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.framework.base.config.ServerConfUtils;
import com.fundevelop.framework.openapi.exception.RestException;
import com.fundevelop.framework.openapi.model.BaseRestRequest;
import com.fundevelop.framework.openapi.model.RestError;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import com.fundevelop.framework.openapi.utils.CmdServiceInvoker;
import com.fundevelop.framework.openapi.utils.ResponseUtils;
import com.fundevelop.framework.openapi.utils.RestCmdHelps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/20 下午9:59
 */
@Controller
@RequestMapping(value = "/uploadFile")
public class UploadController extends BaseController {
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public RestResponse upload(
            @RequestParam(value = "files") MultipartFile[] files,
            @RequestParam(value = "cmd") String cmd,
            @RequestParam(value = "parameters", required = false) String parameters,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "appPushToken", required = false) String appPushToken,
            @RequestParam(value = "appVersion", required = false) String appVersion,
            @RequestParam(value = "clientIdentifierCode", required = false) String clientIdentifierCode,
            @RequestParam(value = "runningMode", required = false) String runningMode,
            @RequestParam(value = "appDomain", required = false) String appDomain,
            @RequestParam(value = "temporary-iframe-id", required = false) String iframeId,
            HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        RestRequest restRequest = new RestRequest();
        restRequest.setFiles(files);
        restRequest.setCmd(cmd);

        if (StringUtils.isNotBlank(parameters)) {
            restRequest.setParameters(BeanUtils.toBean(parameters, Map.class));
        } else {
            restRequest.setParameters(new HashMap<String, Object>(0));
        }

        restRequest.setToken(token);
        restRequest.setAppPushToken(appPushToken);
        restRequest.setAppVersion(appVersion);
        restRequest.setClientIdentifierCode(clientIdentifierCode);
        restRequest.setRunningMode(runningMode);
        restRequest.setAppDomain(appDomain);

        RestResponse restResponse = handleRequest(restRequest, httpRequest);

        if (StringUtils.isBlank(iframeId)) {
            return restResponse;
        }

        PrintWriter out = response.getWriter();
        String jsonString = BeanUtils.toJson(restResponse);
        response.setContentType("text/html; charset=UTF-8");
        out.println("<script language=\"javascript\" type=\"text/javascript\">");
        out.println("parent.$(\"#"+iframeId+"\").data(\"deferrer\").resolve(" + jsonString + ");");
        out.println("</script>");

        return null;
    }

    private RestResponse handleRequest(final RestRequest restRequest, HttpServletRequest httpRequest) {
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
                logger.debug("RECEIVE_REQUEST, uploadFile: {}, ip:{}, thread:{}, token: {}, files:{}, content:\n{}",
                        cgiServiceName, clientIp, threadName, token, request.getFiles()==null?0:request.getFiles().length, BeanUtils.toJson(request));
            } else {
                logger.info("RECEIVE_REQUEST, uploadFile: {}, ip:{}, thread:{}, token: {}, files:{}, content: \n{}",
                        cgiServiceName, clientIp, threadName, token, request.getFiles()==null?0:request.getFiles().length, BeanUtils.toJson(request));
            }
        } catch (Exception ex) {
            logger.warn("记录uploadFile请求日志出现异常", ex);
        }
    }

}
