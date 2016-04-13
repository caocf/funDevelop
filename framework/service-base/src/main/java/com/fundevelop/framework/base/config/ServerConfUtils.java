package com.fundevelop.framework.base.config;

import com.fundevelop.commons.web.utils.IpUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 服务配置工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/18 14:51
 */
public class ServerConfUtils {
    /**
     * 获取服务器配置.
     */
    public static ServerConfiguration getConf() {
        checkInit();

        return conf;
    }

    /**
     * 验证是否已经初始化配置，如果没有将自动进行初始化.
     */
    private static void checkInit() {
        if (!isInit) {
            synchronized (isInit) {
                if (!isInit) {
                    String serviceName = PropertyUtil.get("service.name");

                    if (StringUtils.isBlank(serviceName)) {
                        throw new IllegalStateException("没有在properties配置文件中找到服务名称配置[service.name]，请配置其值为系统对应的业务代码");
                    }

                    String environment = PropertyUtil.get("service.env");
                    String serviceType = PropertyUtil.get("service.type");
                    String group = PropertyUtil.get("service.group");

                    if (StringUtils.isBlank(environment)) {
                        environment = "DEV";
                        logger.warn("没有在properties配置文件中找到服务所处环境配置[service.env]，系统默认为DEV（开发环境）");
                    }
                    if (StringUtils.isBlank(serviceType)) {
                        serviceType = "OPENAPI";
                        logger.warn("没有在properties配置文件中找到服务类别配置[service.type]，系统默认为OPENAPI");
                    }

                    conf.setServiceName(serviceName);
                    conf.setEnvironment(environment);
                    conf.setServiceType(serviceType);
                    conf.setGroup(group);

                    String serverName = "未知";

                    try {
                        serverName = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        logger.warn("无法获取主机名称", e);
                    }

                    conf.setServerName(serverName);
                    conf.setServerIP(IpUtils.getServerIp());
                    conf.setServerPort(IpUtils.getServerPort());
                    conf.setDeployPath(SpringContextHolder.getRootPath());

                    String alarmResponseTime = PropertyUtil.get("alarm.responseTime");

                    if (StringUtils.isNumeric(alarmResponseTime)) {
                        conf.setAlarmResponseTime(Long.parseLong(alarmResponseTime,10));
                    }

                    conf.setManagerEmails(PropertyUtil.get("alarm.receiver.email"));
                    conf.setManagerPhones(PropertyUtil.get("alarm.receiver.phone"));

                    isInit = true;
                }
            }
        }
    }

    private ServerConfUtils() {}

    private static Boolean isInit = false;
    private final static ServerConfiguration conf = new ServerConfiguration();

    private static Logger logger = LoggerFactory.getLogger(ServerConfUtils.class);
}
