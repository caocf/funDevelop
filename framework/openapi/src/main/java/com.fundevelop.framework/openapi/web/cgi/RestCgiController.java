package com.fundevelop.framework.openapi.web.cgi;

import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.model.RestResponse;
import com.fundevelop.framework.openapi.utils.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Restful cgi openapi总入口.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 17:55
 */
@RestController
@RequestMapping(value = "/cgi")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestCgiController {
    /**
     * 初始化CGI.
     */
    public RestCgiController() {
        logger.trace("准备开始初始化CGI服务...");


        logger.trace("CGI服务初始化完毕.");
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
    public RestResponse handleRequest(@RequestBody final RestRequest restRequest, HttpServletRequest httpRequest) {
        return null;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
