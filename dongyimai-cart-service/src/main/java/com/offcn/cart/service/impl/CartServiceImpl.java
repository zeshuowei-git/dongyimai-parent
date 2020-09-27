package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

//        根据商品的SKUID来查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品无效");
        }
//        2获得商家的ID
        String sellerId = item.getSellerId();
//        根据ID判断购物车列表是否存在该商家的购物车
        Cart cart=searchCartBySellerId(cartList,sellerId);
//        购物车列表不存在该商家的购物车
        if (cart==null){
//构建购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem=createOrderItem(item,num);
            List orderItemList=new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
//            将购物车对象添加到购物车列表
            cartList.add(cart);
        }else {
//            如果购物车中有该商家的购物车
//            判断该购物车中是否有商品
            TbOrderItem orderItem=searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if (orderItem==null){
//                如果没有 增加购物车明细
                orderItem=createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else {
//                如果有在原购物车上增加数量 更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue())  );
//                   如果操作后数字小于0.则删除
                   if (orderItem.getNum()<=0){
                       cart.getOrderItemList().remove(orderItem);
                   };
//                   如果移除后cart的明细数量为0，则将cart移除
                   if (cart.getOrderItemList().size()==0){
                       cartList.remove(cart);
                   }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart:cartList2){
            for (TbOrderItem orderItem:cart.getOrderItemList()){
                cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }

        return cartList1;
    }

    /**
     * 根据商家ID查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart:cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    /*
     * 根据商品明细ID查询
     * */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem>orderItemList,Long itemId){
        for (TbOrderItem orderItem:orderItemList){
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    };
/*
* 创建订单明细
* */
    private  TbOrderItem createOrderItem(TbItem item,Integer num){
        if (num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }
}
