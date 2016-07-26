package com.fundevelop.framework.erp.security.shiro;

import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * .
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/20 12:14
 */
@Component
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
    public static final String DEFAULT_CLIENT_CODE_PARAM = "clientCode";
    private String captchaParam = DEFAULT_CAPTCHA_PARAM;
    private static String clientCodeParam = DEFAULT_CLIENT_CODE_PARAM;

    public String getCaptchaParam() {
        return captchaParam;
    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }

    public static String getClientCodeParam() {
        return clientCodeParam;
    }

    public void setClientCodeParam(String clientCodeParam) {
        ShiroFormAuthenticationFilter.clientCodeParam = clientCodeParam;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token,
                                     Subject subject, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            resp.sendRedirect("/main.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token,
                                     AuthenticationException e, ServletRequest request,
                                     ServletResponse response) {
        logger.info(" 登录失败的原因为: {}", e.getMessage());

        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            resp.sendRedirect("/login.html?error=" + e.getMessage());
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, java.lang.Object)
     * @author <a href="mailto:yangmujiang@xiaomashijia.com">Reamy(杨木江)</a>
     * @date 2015-05-23 19:51:49
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);

        //如果 isAuthenticated 为 false 证明不是登录过的，同时 isRememberd 为true 证明是没登陆直接通过记住我功能进来的
        if (!subject.isAuthenticated() && subject.isRemembered()) {
            String userId = (String)subject.getPrincipal();

            if (StringUtils.isNotBlank(userId)) {
                SysUserToken sysUser = sysUserCheck.getByUserId(userId);

                if (sysUser != null && sysUser.isEnabled()) {
                    subject.login(new UsernamePasswordToken(sysUser.getLoginName(), sysUser.getPassword(), true));

                    return subject.isRemembered();
                }
            }
        }

        return subject.isAuthenticated();
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        if (com.fundevelop.commons.utils.StringUtils.isBooleanTrue(PropertyUtil.get("erp.sysuser.login.useCaptcha", "0"))) {
            String username = getUsername(request);
            String password = getPassword(request);
            String captcha = getCaptcha(request);
            String clientCode = getClientCode(request);
            boolean rememberMe = isRememberMe(request);
            String host = getHost(request);

            return new UsernamePasswordCaptchaToken(username, password, rememberMe, host, captcha, clientCode);
        } else {
            return super.createToken(request, response);
        }
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    protected String getClientCode(ServletRequest request) {
        return WebUtils.getCleanParam(request, getClientCodeParam());
    }

    @Autowired
    private SysUserCheck sysUserCheck;
}
