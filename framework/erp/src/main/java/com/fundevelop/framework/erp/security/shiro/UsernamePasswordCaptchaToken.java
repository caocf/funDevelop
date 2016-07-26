package com.fundevelop.framework.erp.security.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 用户登录Token(带验证码).
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/5 11:07
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {
    private String captcha;
    private String clientCode;

    public UsernamePasswordCaptchaToken() {
        super();
    }

    public UsernamePasswordCaptchaToken(final String username, final String password,
                                        final boolean rememberMe, final String host, final String captha, final String clientCode) {
        super(username, password, rememberMe, host);
        this.captcha = captha;
        this.clientCode = clientCode;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}
