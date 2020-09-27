package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 导入solr数据监听器
 * @author ：yz
 * @date ：Created in 2020/9/11 14:56
 * @version: 1.0
 */
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message) {
        try {
            //转换
            TextMessage textMessage=(TextMessage) message;
            //转换成集合
            List<TbItem> items = JSONArray.parseArray(textMessage.getText(), TbItem.class);
            //导入索引库

            //显示数据
            for (TbItem item : items) {
                //处理sku
                String spec = item.getSpec();
                if(spec!=null&&!"".equals(spec)){
                    //记录拼音map
                    Map<String,String> pinMap=new HashMap<>();
                    //转换成map
                    //{"机身内存":"128G","网络":"电信4G"}
                    Map<String,String> map = JSON.parseObject(spec, Map.class);
                    //将key转换成拼音
                    Set<String> keySet = map.keySet();
                    //遍历key
                    for (String key : keySet) {
                        //将key转换成拼音
                        String pinyinKey = Pinyin.toPinyin(key, "").toLowerCase();
                        //存储
                        pinMap.put(pinyinKey,map.get(key));
                    }
                    //设置Sku对应的域字段
                    item.setSpecMap(pinMap);
                }


            }
            //导入到solr索引库
            itemSearchService.improtItemList(items);


            System.out.println("导入到索引库");

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
