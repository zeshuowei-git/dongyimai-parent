package com.offcn.page.service;

/**
 * 商品详情页接口
 */
public interface ItemPageService {


    /**
     * 删除详情页
     * @param goodsId
     * @return
     */
    public boolean deletePageHtml(Long goodsId);

    /**
     *  商品详情页面
     * @param goodsId
     * @return
     */
    public boolean getItemHtml(Long goodsId);
}
