package com.fundevelop.framework.erp.security.shiro;

import java.util.List;

/**
 * 系统用户身份认证接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/21 12:53
 */
public interface SysUserCheck {
    /**
     * 根据登录名和密码获取用户.
     * @param loginName 登录名
     * @param password 密码
     * @return 对应的用户
     */
    SysUserToken getUser(String loginName, String password);

    /**
     * 获取用户登录返回信息.
     */
    Object getLoginResponse(String userId);

    /**
     * 根据用户ID获取用户.
     */
    SysUserToken getByUserId(String userId);

    /**
     * 根据用户ID获取用户拥有的角色名称.
     */
    List<String> getRoleName(String userId);

    /**
     * 根据用户ID获取用户拥有的资源名称.
     */
    List<String> getResourceName(String userId);

    /**
     * 根据用户ID获取用户拥有的组织名称.
     */
    List<String> getOrgName(String userId);
}
