package com.fundevelop.framework.erp.security.shiro;

import java.io.Serializable;

/**
 * 系统用户Token Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/21 12:50
 */
public class SysUserToken implements Serializable {
    /** 用户ID */
    private String userId;
    /** 用户登录名 */
    private String loginName;
    /** 密码 */
    private String password;
    /** 是否有效 */
    private boolean enabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
