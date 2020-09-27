package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 搜索的接口
 */
public interface ItemSearchService {


    /**
     *  删除索引库中指定的商品
     * @param ids
     */
    public void deleteByGoodsIds(List ids);

    /**
     * 导入到索引库solr
     * @param itemListe
     */
    public void improtItemList(List<TbItem> itemListe);

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map<String,String> searchMap);

}
