package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * @author ：yz
 * @date ：Created in 2020/9/4 15:15
 * @version: 1.0
 */
@Service
public class ItemSarchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 删除索引库中指定的商品
     *
     * @param ids
     */
    @Override
    public void deleteByGoodsIds(List ids) {

        System.out.println("删除的SPUid+"+ids);
        //创建一个查询器
        Query query=new SimpleQuery("*:*");
        //添加过滤条件
        Criteria criteria=new Criteria("item_goodsid").in(ids);
        //添加到查询器
        query.addCriteria(criteria);
        //删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();


    }

    /**
     * 导入到索引库solr
     *
     * @param items
     */
    @Override
    public void improtItemList(List<TbItem> items) {

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

    /**
     * 搜索
     *
     * @param searchMap
     * @return .keywords=手机
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

     /*   //拼接查询条件
        Query query=new SimpleQuery("*:*");

        //创建条件
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));

        //添加
        query.addCriteria(criteria);

        //查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //创建map
        Map<String,Object> resultMap=new HashMap<>();

        resultMap.put("rows",scoredPage.getContent());*/


        //进行空格的处理
        String keywords = searchMap.get("keywords");
        //取出空字符
        String replace = keywords.replace(" ", "");
        //重新赋值
        searchMap.put("keywords",replace);

        //构建返回值结果
        Map<String, Object> resultMap = new HashMap<>();


        //1.高亮调用
        Map<String, Object> map = searchList(searchMap);

        //获取返回值
        resultMap.putAll(map);

        //2.查询分类列表

        List<String> categoryList = searchCategoryList(searchMap);

        //存储分类列表
        resultMap.put("categoryList",categoryList);

        //默认获取第一个分类名称
        if(categoryList!=null&&categoryList.size()>0){


            Map<String, Object> brandAndSpecList=null;
            if(searchMap.get("category")!=null&&!"".equals(searchMap.get("category"))){

                //获取规格列表和品牌列表
                brandAndSpecList= searchBrandAndSpecList(searchMap.get("category"));

            }else{

                //获取规格列表和品牌列表
                brandAndSpecList = searchBrandAndSpecList(categoryList.get(0));
            }






            //存储到返回值
            resultMap.putAll(brandAndSpecList);

        }


        return resultMap;
    }


    //根据分类名称获取品牌列表和规格列表
    private Map<String,Object> searchBrandAndSpecList(String category){

        //构建查询结果
        Map<String,Object> brandAndSpecList=new HashMap<>();

        //根据分类名称到redis中查询模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //判断
        if(typeId!=null){

            //获取品牌列表
            List<Map> brandList =(List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);

            //存储
            brandAndSpecList.put("brandList",brandList);

            //获取规格列表
            List<Map> specList = (List<Map>)redisTemplate.boundHashOps("specList").get(typeId);

            //存储
            brandAndSpecList.put("specList",specList);

        }




        return brandAndSpecList;


    }

    //分组查询商品分类列表
    private List<String> searchCategoryList(Map<String,String> searchMap){

        //构建结果集合
        List<String> categoryList=new ArrayList<>();


        //普通查询器
        Query query=new SimpleQuery();

        //设置条件
        //创建条件
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));

        //添加
        query.addCriteria(criteria);


        //设置查询分组
        GroupOptions groupOptions=new GroupOptions();
        groupOptions.addGroupByField("item_category");
        //关联分组
        query.setGroupOptions(groupOptions);

        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果集合
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取结果
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历
        for (GroupEntry<TbItem> entry : content) {

            System.out.println(entry.getGroupValue());

            //存储到列表中
            categoryList.add(entry.getGroupValue());

        }


        //获取分组列表


        return categoryList;


    }



    //查询高亮效果实现
    private Map<String, Object> searchList(Map<String, String> searchMap) {

        //构建返回结果map
        Map<String,Object> map=new HashMap<>();


        //构建高亮查询器
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置高亮字段
        HighlightOptions options =new HighlightOptions();
        options.addField("item_title");
        //设置前缀
        options.setSimplePrefix("<em style='color:red'>");
        //设置后缀
        options.setSimplePostfix("</em>");
        //关联查询器
        query.setHighlightOptions(options);

        //设置条件对象
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联到查询器
        query.addCriteria(criteria);

        //添加品牌条件

        if(searchMap.get("brand")!=null&&!"".equals(searchMap.get("brand"))){

            //创建条件
            Criteria filtercriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            //创建一个过滤对象
            FilterQuery filterQuery=new SimpleFilterQuery();
            //关联到过滤条件
            filterQuery.addCriteria(filtercriteria);
            //关联到高亮查询器
            query.addFilterQuery(filterQuery);
        }
        //添加分类

        if(searchMap.get("category")!=null&&!"".equals(searchMap.get("category"))){
        //创建条件
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            //创建一个过滤对象
            FilterQuery filterQuery=new SimpleFilterQuery();
            //添加条件
            filterQuery.addCriteria(filterCriteria);
            //关联到高亮查询器
            query.addFilterQuery(filterQuery);


        }




        //添加规格条件
        String spec = searchMap.get("spec");
        if(spec!=null&&!"".equals(spec)){
            //将规格字符串转换成规格对象
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);

            Set<String> keySet = specMap.keySet();
            //遍历
            for (String key : keySet) {

                //拼接item_spec_jishenneicun

                String feild="item_spec_"+ Pinyin.toPinyin(key,"").toLowerCase();

                //拼接条件
                Criteria filterCriteria=new Criteria(feild).is(specMap.get(key));
                //过滤查询器对象
                FilterQuery filterQuery=new SimpleFilterQuery();
                //添加条件
                filterQuery.addCriteria(filterCriteria);
                //关联到高亮的查询器
                query.addFilterQuery(filterQuery);


            }


        }




        //添加价格条件
        //获取价格
        String price = searchMap.get("price");
        //判断
        if(!"".equals(price)&&price!=null){
            //截取
            String[] prices = price.split("-");
            //获取开始价格
             String startPrice=prices[0];
            //获取结束价格
            String endPrice=prices[1];


            //开始价格条件添加
            if (!"0".equals(startPrice)) {

                //条件对象
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(startPrice);
                //过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //关联条件
                filterQuery.addCriteria(filterCriteria);
                //关联查询器
                query.addFilterQuery(filterQuery);

            }


            //结束价格添加
            if(!"*".equals(endPrice)){

                Criteria filterCriteria=new Criteria("item_price").lessThanEqual(endPrice);
                //过滤对象
                FilterQuery filterQuery=new SimpleFilterQuery();
                //关联条件
                filterQuery.addCriteria(filterCriteria);
                //关联查询器
                query.addFilterQuery(filterQuery);



            }



        }

        //分页条件添加

        //获取当前页
        String pageNoStr = searchMap.get("pageNo");
        Integer pageNo=null;

        if(!"".equals(pageNoStr)&&pageNoStr!=null){
           pageNo = Integer.parseInt(pageNoStr);

        }else{
           pageNo =1;
        }
        
        //获取每页条数
        String pageSizeStr = searchMap.get("pageSize");
        Integer pageSize=null;
        if(!"".equals(pageSizeStr)&&pageSizeStr!=null){

            pageSize=Integer.parseInt(pageSizeStr);

        }else{
            pageSize=20;

        }


        //排序
        //获取排序字段
        String sortField = searchMap.get("sortField");
        //获取排序方式
        String sort = searchMap.get("sort");

        //判断
        if(!"".equals(sort)&&!"".equals(sortField)&&sortField!=null&&sort!=null){

            //降序添加
            if("DESC".equals(sort)){

                //创建排序对象
                Sort sortCriteria=new Sort(Sort.Direction.DESC,"item_"+sortField);
                //添加到查询器
                query.addSort(sortCriteria);

            }
            //降序添加
            if("ASC".equals(sort)){

                //创建排序对象
                Sort sortCriteria=new Sort(Sort.Direction.ASC,"item_"+sortField);
                //添加到查询器
                query.addSort(sortCriteria);

            }



        }






        //设置开始索引
        query.setOffset((pageNo-1)*pageSize);

        //设置每页条数
        query.setRows(pageSize);




        //使用高亮查询
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取查询结果
        List<TbItem> content = page.getContent();
        //遍历
        for (TbItem item : content) {

            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);

            //判断
            if(highlights!=null&&highlights.size()>0){

                System.out.println();

                //获取高亮字段内容
                String HighlightContent = highlights.get(0).getSnipplets().get(0);

                //设置高亮字段
                item.setTitle(HighlightContent);

            }



        }


        //添加到返回值：
        map.put("rows",content);
        //获取总记录数据
        map.put("total",page.getTotalElements());
        //获取总页数
        map.put("totalPages",page.getTotalPages());
        return map;
    }

}
