package com.fundevelop.framework.base.config;

/**
 * 服务器配置信息.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/17 8:21
 */
public class ServerConfiguration {
    /** 环境(开发、UAT、堡垒、生产). */
    protected String environment;
    /** 服务类别(OPENAPI、ERP、RPC、JOB). */
    protected String serviceType;
    /** 服务名称. */
    protected String serviceName;
    /** 所属分组. */
    protected String group;

    /** 服务器名称. */
    protected String serverName;
    /** 服务器IP地址. */
    protected String serverIP;
    /** 服务端口. */
    protected Integer serverPort;
    /** 部署路径. */
    protected String deployPath;

    /** 响应报警阀值（当请求处理时间超过该阀值时将向管理员发出报警信息），单位毫秒 */
    protected long alarmResponseTime;
    /** 管理员邮件，多个用“,”分割. */
    protected String managerEmails;
    /** 管理员手机号码，多个用“,”分割. */
    protected String managerPhones;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getManagerEmails() {
        return managerEmails;
    }

    public void setManagerEmails(String managerEmails) {
        this.managerEmails = managerEmails;
    }

    public String getManagerPhones() {
        return managerPhones;
    }

    public void setManagerPhones(String managerPhones) {
        this.managerPhones = managerPhones;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getDeployPath() {
        return deployPath;
    }

    public void setDeployPath(String deployPath) {
        this.deployPath = deployPath;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getAlarmResponseTime() {
        return alarmResponseTime;
    }

    public void setAlarmResponseTime(long alarmResponseTime) {
        this.alarmResponseTime = alarmResponseTime;
    }
}
