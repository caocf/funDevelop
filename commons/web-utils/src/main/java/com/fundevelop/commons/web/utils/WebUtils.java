package com.fundevelop.commons.web.utils;

import com.fundevelop.commons.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Web辅助工具.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/6 18:55
 */
public class WebUtils {
    /**
     * 获取IP地址.
     */
    public static String getIpAddr(HttpServletRequest request) {
        HttpSession session = null;
        String ip = null;

        if (StringUtils.isBooleanTrue(PropertyUtil.get("fun.webUtils.cacheIp", "false"))) {
            session = request.getSession(false);

            if (session != null) {
                ip = (String)session.getAttribute(IP_KEY);
            }
        }

        if (ip == null) {
            ip = request.getHeader("x-forwarded-for");

            if (ip != null && !"unknown".equalsIgnoreCase(ip)) {
                ip = ip.split(",")[0];
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            if (session != null) {
                session.setAttribute(IP_KEY, ip);
            }
        }

        return ip;
    }

    /** Session中IP地址对应的键值. */
    private static final String IP_KEY = "_ARCH_CACHE_IP_KEY_";

    private WebUtils(){}
}
