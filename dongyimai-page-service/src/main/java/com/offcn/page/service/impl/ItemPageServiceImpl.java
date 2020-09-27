package com.offcn.page.service.impl;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：yz
 * @date ：Created in 2020/9/9 13:42
 * @version: 1.0
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 删除详情页
     *
     * @param goodsId
     * @return
     */
    @Override
    public boolean deletePageHtml(Long goodsId) {

        //删除页面
        boolean flag = new File(pagedir+goodsId + ".html").delete();


        return flag;
    }

    /**
     * 商品详情页面
     *
     * @param goodsId
     * @return
     */
    @Override
    public boolean getItemHtml(Long goodsId) {
        try {
            //获取核心配置类
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //获取模板
            Template template = configuration.getTemplate("item.ftl");
            //设置商品模型
            Map map=new HashMap();
            //查询goods数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            //存储到map
            map.put("goods",tbGoods);
            //查询goodsDesc数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //存储到map
            map.put("goodsDesc",goodsDesc);

            //查询三级分类数据

            //一级
            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            //二级
            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            //三级
            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            //存储三级分类
            map.put("itemCat1",itemCat1);
            map.put("itemCat2",itemCat2);
            map.put("itemCat3",itemCat3);

            //查询SKU信息
            //创建条件对象
            TbItemExample example=new TbItemExample();
            //添加条件
            TbItemExample.Criteria criteria = example.createCriteria();
            //等于goodsId
            criteria.andGoodsIdEqualTo(goodsId);
            //状态可用 status1
            criteria.andStatusEqualTo("1");
            //添加排序
            example.setOrderByClause("is_default desc");

            //查询
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //存储
            map.put("itemList",itemList);


            //获取输入出流Writer
            //d:\\item\\149187842868059.html
            Writer out=new FileWriter(pagedir+goodsId+".html");
            //生成页面
            template.process(map,out);
            //关闭流
            out.close();

            System.out.println("页面生成=="+goodsId+".html");
            return true;
        } catch (Exception e) {


            e.printStackTrace();
            return false;

        }
    }
}
