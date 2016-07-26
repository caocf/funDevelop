package com.fundevelop.framework.erp.frame.web.Ace.model;

import java.io.Serializable;

/**
 * Editable控件验证结果Bean.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/18 14:31
 */
public class EditableCheck implements Serializable {
    private boolean sucess = true;
    private String msg;

    public EditableCheck(boolean sucess) {
        this(sucess, null);
    }

    public EditableCheck(boolean sucess, String msg) {
        this.sucess = sucess;
        this.msg = msg;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
