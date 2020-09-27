package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

/*
* 购物车服务接口
* */
public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /*
    * 在redis中查询购物车
    * */
    public List<Cart> findCartListFromRedis(String username);
    /*
    * 将购物车保存在redis中
    * */
    public void saveCartListToRedis(String username,List<Cart> cartList);
    /*
    * 合并购物车
    * */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
