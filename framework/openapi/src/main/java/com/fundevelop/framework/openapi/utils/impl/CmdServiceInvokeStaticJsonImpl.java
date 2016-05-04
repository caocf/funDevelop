package com.fundevelop.framework.openapi.utils.impl;

import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.framework.openapi.model.BaseRestRequest;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import com.fundevelop.framework.openapi.staticjson.CmdStaticJsonService;
import com.fundevelop.framework.openapi.utils.CmdServiceInvoker;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * OpenAPI cmd服务代理接口静态Json文件方式返回实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/3 21:57
 */
public class CmdServiceInvokeStaticJsonImpl implements CmdServiceInvoker {
    private BaseRestRequest request;
    private String basePath = null;

    public CmdServiceInvokeStaticJsonImpl(BaseRestRequest request) {
        this.request = request;
        String bp = PropertyUtil.get("fun.openapi.staticJson.basePath");

        if (StringUtils.isBlank(bp)) {
            throw new RuntimeException("开启静态JSON文件模式时，需要在properties中配置fun.openapi.staticJson.basePath属性（静态JSON文件根目录地址）");
        }

        basePath = bp;
    }

    @Override
    public void invoke(RestRequest request, RestResponse response) {
        Object responseBody = null;
        String jsonPath = basePath;

        try {
            if (basePath.endsWith("/")) {
                if (request.getCmd().startsWith("/")) {
                    jsonPath += request.getCmd().substring(1);
                } else {
                    jsonPath += request.getCmd();
                }
            } else {
                if (request.getCmd().startsWith("/")) {
                    jsonPath += request.getCmd();
                } else {
                    jsonPath += "/" + request.getCmd();
                }
            }

            URL jsonFileUrl = new URL(jsonPath);

            System.out.println("static json file protocol="+jsonFileUrl.getProtocol());

            CmdStaticJsonService staticJsonService = (CmdStaticJsonService)SpringContextHolder.getBean("staticJson."+jsonFileUrl.getProtocol().toLowerCase());

            if (staticJsonService != null) {
                responseBody = staticJsonService.getContent(jsonFileUrl);
            } else {
                throw new RuntimeException("不支持的静态文件协议：" + jsonFileUrl.getProtocol() + "，basePaht：" + basePath  + "，cmd：" + request.getCmd());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("获取静态JSON文件发生异常，basePaht：" + basePath  +"，cmd：" + request.getCmd(), e);
        } finally {
            response.setResponse(responseBody);
            Date responseTime = new Date();
            response.setResponseTime(responseTime);
            response.setDuration(responseTime.getTime() - request.getRequestTime().getTime());
        }
    }
}
