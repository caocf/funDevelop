package com.fundevelop.framework.openapi.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.UuidGenerator;
import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.config.ServerConfUtils;
import com.fundevelop.framework.openapi.model.RestRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

/**
 * 基础控制类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/21 上午10:16
 */
public abstract class BaseController {
    /**
     * 初始化请求.
     */
    protected void initRequest(RestRequest restRequest, HttpServletRequest httpRequest) {
        if (restRequest != null) {
            // 唯一ID
            restRequest.setRequestId(UuidGenerator.getNextId());

            // 请求时间
            restRequest.setRequestTime(new Date());

            // 记录客户端IP
            if (StringUtils.isBlank(restRequest.getIp())) {
                restRequest.setIp(IpUtils.getIpAddr(httpRequest));
            }

            // debug 开关
            if (StringUtils.equalsIgnoreCase("true", PropertyUtil.get("fun.openapi.forceTurnOffDebug"))) {
                restRequest.setDebug(false);
            }
        }
    }


    protected Logger logger = LoggerFactory.getLogger(getClass());
}
