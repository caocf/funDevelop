package com.fundevelop.framework.erp.frame.datamodel.autocomplete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fundevelop.framework.erp.frame.impl.DefaultDataModel;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * autocomplete数据Bean.
 * <p>描述:存储autocomplete数据集</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/30 17:46
 */
public class AutocompleteModel<T extends BaseEntity<ID>, ID extends Serializable> extends DefaultDataModel<T, ID> {
    @Override
    @JsonIgnore
    public int getPage() {
        return super.getPage();
    }

    @Override
    @JsonIgnore
    public int getPageSize() {
        return super.getPageSize();
    }

    @Override
    @JsonIgnore
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    @JsonIgnore
    public int getTotalPages() {
        return super.getTotalPages();
    }

    @Override
    public void setData(List<T> dataList) {
        if(dataList!=null&&dataList.size()>0){
            data = new ArrayList<>(dataList.size());
            AutocompleteBuild build = new AutocompleteBuild(getFilter());
            for (T bean : dataList) {
                build.convert(bean, data);
            }
            build = null;
        }
    }
}
