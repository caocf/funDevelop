package com.fundevelop.framework.erp.security.shiro;

import com.fundevelop.cache.redis.CaptchaUtils;
import com.fundevelop.commons.utils.SecurityUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * 验证用户.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/11 15:05
 */
public class SecurityRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userId = (String) getAvailablePrincipal(principalCollection);

        if (StringUtils.isBlank(userId)) {
            return null;
        }

        SysUserToken sysUser = sysUserCheck.getByUserId(userId);

        if (sysUser == null) {
            return null;
        }

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户拥有的角色
        authorizationInfo.addRoles(sysUserCheck.getRoleName(userId));

        // 获取用户所属组织
        authorizationInfo.addRoles(sysUserCheck.getOrgName(userId));

        // 获取用户拥有的资源
        authorizationInfo.addStringPermissions(sysUserCheck.getResourceName(userId));

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String captcha = null;
        String clientCode = null;

        if (com.fundevelop.commons.utils.StringUtils.isBooleanTrue(PropertyUtil.get("erp.sysuser.login.useCaptcha", "0"))) {
            if (token instanceof UsernamePasswordCaptchaToken) {
                captcha = ((UsernamePasswordCaptchaToken)token).getCaptcha();
                clientCode = ((UsernamePasswordCaptchaToken)token).getClientCode();

                if (captcha == null) {
                    captcha = "";
                }
            }
        }

        if (captcha != null) {
            if (StringUtils.isBlank(clientCode)) {
                logger.error("开启图片验证登录功能,登录时没有提交终端识别码参数:clientCode");
                throw new AuthenticationException(SecurityUtils.LOGIN_FAIL_INVALID_CAPTCHA);
            }

            String relCaptchaCode = CaptchaUtils.getCaptcha(clientCode);

            if (!StringUtils.equals(captcha, relCaptchaCode)) {
                logger.warn("图片验证码不正确，客户端唯一标示：{}，验证码：{}，正确验证码：{}",  clientCode, captcha, relCaptchaCode);
                throw new AuthenticationException(SecurityUtils.LOGIN_FAIL_INVALID_CAPTCHA);
            }
        }

        String username = usernamePasswordToken.getUsername();
        String password = new String(usernamePasswordToken.getPassword());
        String md5PWD = password;

        if (com.fundevelop.commons.utils.StringUtils.isBooleanTrue(PropertyUtil.get("erp.password.isPlaintext", "true"))) {
            try {
                md5PWD = SecurityUtils.createMd5Password(password, PropertyUtil.get("erp.sysuser.password.salt", ""));
            } catch (NoSuchAlgorithmException e) {
                logger.error("验证用户时对用户密码进行MD5加密失败", e);
                throw new AuthenticationException(SecurityUtils.LOGIN_FAIL_INVALID_ACCOUNT);
            }
        }

        SysUserToken sysUser = sysUserCheck.getUser(username, md5PWD);

        if (sysUser == null) {
            sysUser = sysUserCheck.getUser(username, password);

            if (sysUser == null) {
                throw new AuthenticationException(SecurityUtils.LOGIN_FAIL_INVALID_ACCOUNT);
            }
        }

        // 禁用的账号添加拦截
        if (!sysUser.isEnabled()) {
            logger.warn("账号 {} 已经被禁用，尝试登录！", sysUser.getUserId());
            throw new AuthenticationException(SecurityUtils.LOGIN_FAIL_DISABLED);
        }

        return new SimpleAuthenticationInfo(sysUser.getUserId(), password, getName());
    }

    protected Object getAvailablePrincipal(PrincipalCollection principalCollection) {
        Object primary = null;
        if (!org.apache.shiro.util.CollectionUtils.isEmpty(principalCollection)) {
            Collection thisPrincipals = principalCollection.fromRealm(getName());
            if (!org.apache.shiro.util.CollectionUtils.isEmpty(thisPrincipals)) {
                primary = thisPrincipals.iterator().next();
            } else {
                //no principals attributed to this particular realm.  Fall back to the 'master' primary:
                primary = principalCollection.getPrimaryPrincipal();
            }
        }

        return primary;
    }

    @Autowired
    private SysUserCheck sysUserCheck;
    private Logger logger = LoggerFactory.getLogger(getClass());
}
