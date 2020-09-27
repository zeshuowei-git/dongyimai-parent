package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：yz
 * @date ：Created in 2020/9/4 13:52
 * @version: 1.0
 * SolrUtil-->solrUtil
 */
@Component
public class SolrUtil {


    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 实现将数据库的SKU数据导入到solr库中
     * <p>
     * 1.查询所有mysql中sku的信息
     * 2.整合solr,导入数据daosolr库中
     * 3.添加动态域数据
     */
    public void importItems() {

        //查询条件
        TbItemExample example = new TbItemExample();
        //审核通过
        example.createCriteria().andStatusEqualTo("1");
        //查询
        List<TbItem> items = itemMapper.selectByExample(example);


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

        //导入solr库
        solrTemplate.saveBeans(items);
        //提交
        solrTemplate.commit();




    }

    public static void main(String[] args) {

        //加载spring容器
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/*.xml");

        //获取指定的bean
        SolrUtil solrUtil = (SolrUtil)context.getBean("solrUtil");
        //执行方法
        solrUtil.importItems();
    }



}
