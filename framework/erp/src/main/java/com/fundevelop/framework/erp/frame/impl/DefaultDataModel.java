package com.fundevelop.framework.erp.frame.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fundevelop.framework.erp.frame.FrameDataModel;
import com.fundevelop.framework.erp.frame.datamodel.helps.JacksonBean;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 默认数据模型实现类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/27 17:15
 */
public class DefaultDataModel<T extends BaseEntity<ID>, ID extends Serializable> extends JacksonBean implements FrameDataModel<T, ID> {
    @Override
    public Object getData() {
        return data;
    }

    /**
     * 获取当前页号.
     */
    public int getPage() {
        return page;
    }

    /**
     * 获取每页记录数.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 获取总记录数.
     */
    public long getTotal() {
        return total;
    }

    /**
     * 获取总页数.
     */
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    @JsonIgnore
    public boolean isUseDataOfRoot() {
        return false;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;

        if (data != null) {
            this.pageSize = data.size();
            this.total = this.pageSize;
        }
    }

    @Override
    public void setData(Page<T> page) {
        if (page != null) {
            setData(page.getContent());
            this.page = page.getNumber()+1;
            this.pageSize = page.getSize();
            this.totalPages = page.getTotalPages();
            this.total = page.getTotalElements();
        }
    }

    @Override
    public void setData(Object data) {
        if (data instanceof List) {
            try {
                setData((List<T>) data);
            } catch (ClassCastException ex) {
                this.data = data;
            }
        } else if (data instanceof Page) {
            try {
                setData((Page<T>) data);
            } catch (ClassCastException ex) {
                this.data = data;
            }
        } else {
            this.data = data;
        }
    }

    @Override
    public void setData(Page<T> page, List<T> data) {
        if (data == null || data.isEmpty()) {
            setData(page);
        } else if (page != null) {
            setData(data);
            this.page = page.getNumber()+1;
            this.pageSize = page.getSize();
            this.totalPages = page.getTotalPages();
            this.total = page.getTotalElements();
        }
    }

    @Override
    @JsonIgnore
    public Object getResponse() {
        return isUseDataOfRoot()?data:this;
    }

    /** 数据. */
    protected Object data;
    /** 当前页号. */
    protected int page = 1;
    /** 每页记录数. */
    protected int pageSize;
    /** 总记录数. */
    protected long total = 0;
    /** 总页数. */
    protected int totalPages = 1;
}
