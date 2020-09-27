package com.offcn.entity;

import com.offcn.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车实体类
 * @author ：yz
 * @date ：Created in 2020/9/16 10:24
 * @version: 1.0
 */
public class Cart implements Serializable {

    //商家id
    private String sellerId;
    //商家名称
    private String sellerName;
    //购物项明细列表
    private List<TbOrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "sellerId='" + sellerId + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", orderItemList=" + orderItemList +
                '}';
    }
}
