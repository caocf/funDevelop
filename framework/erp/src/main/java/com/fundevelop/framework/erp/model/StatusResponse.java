package com.fundevelop.framework.erp.model;

/**
 * 状态返回信息Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/6/7 08:52
 */
public class StatusResponse {
    private int statusCode = 0;
    private String msg;

    public StatusResponse(int statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
