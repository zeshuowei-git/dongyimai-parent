package com.offcn.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：yz
 * @date ：Created in 2020/8/25 14:00
 * @version: 1.0
 */
public class PageResult implements Serializable {

    private Long total;//总记录数

    private List rows;//每页数据

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
