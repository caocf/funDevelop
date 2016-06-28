package com.fundevelop.framework.erp.web;

import com.fundevelop.commons.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全验证控制类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/22 22:21
 */
@Controller
public class SecurityController {
    @RequestMapping(value = "/login")
    @ResponseBody
    public Map<String, Object> login(String error) {
        Map<String, Object> response = new HashMap<>(1);
        Map<String, Object> security = new HashMap<>(2);

        if (StringUtils.isBlank(error)) {
            error = "0";
        }

        String msg = "";

        if (SecurityUtils.LOGIN_FAIL_INVALID_ACCOUNT.equals(error)) {
            msg = "用户账号密码错误";
        } else if (SecurityUtils.LOGIN_FAIL_DISABLED.equals(error)) {
            msg = "用户被禁用";
        } else if (!"0".equals(error)) {
            msg = error;
            error = "3";
        }

        security.put("code", error);
        security.put("msg", msg);

        response.put("erpSecurity", security);

        return response;
    }
}
