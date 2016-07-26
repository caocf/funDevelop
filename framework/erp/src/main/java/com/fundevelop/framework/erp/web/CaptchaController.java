package com.fundevelop.framework.erp.web;

import com.fundevelop.cache.redis.CaptchaUtils;
import com.fundevelop.framework.erp.security.shiro.ShiroFormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 输出图片验证码.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/5 11:47
 */
@Controller
@RequestMapping(value = "/common/captcha")
public class CaptchaController {
    @RequestMapping
    public void createCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String clientCode = getClientCode(request);

        CaptchaUtils.writeCaptcha(clientCode, response);
    }

    protected String getClientCode(ServletRequest request) {
        return WebUtils.getCleanParam(request, ShiroFormAuthenticationFilter.getClientCodeParam());
    }
}
