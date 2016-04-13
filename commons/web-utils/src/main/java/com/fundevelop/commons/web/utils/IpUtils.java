package com.fundevelop.commons.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

/**
 * IP工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/14 20:48
 */
public class IpUtils {
    /**
     * 获取本机IP地址.
     */
    public static String getServerIp() {
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            logger.error("获取网络信息失败: ", e);
            return "";
        }

        while (e.hasMoreElements()) {
            NetworkInterface x = e.nextElement();
            Enumeration<InetAddress> inetAddresses = x.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                String hostAddress = inetAddresses.nextElement().getHostAddress();
                if (hostAddress.equals("127.0.0.1")) {
                    continue;
                }
                String[] split = hostAddress.split("\\.");
                if (split.length == 4) {
                    return hostAddress;
                }
            }
        }

        return null;
    }

    /**
     * 获取当前容器的端口号
     */
    public static Integer getServerPort() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objs;
        try {
            objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        } catch (MalformedObjectNameException e) {
            logger.error("get server port fail", e);
            return null;
        }

        ObjectName next = objs.iterator().next();
        if (next != null) {
            String port = next.getKeyProperty("port");
            return new Integer(port);
        }
        return null;
    }

    /**
     * 获取客户端IP地址.
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private IpUtils(){}

    private static Logger logger = LoggerFactory.getLogger(IpUtils.class);
}
