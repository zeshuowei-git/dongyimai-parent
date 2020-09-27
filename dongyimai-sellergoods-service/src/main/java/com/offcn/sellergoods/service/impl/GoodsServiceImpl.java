package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 商品服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 根据SPU和审核状态查询SKU
     *
     * @param ids
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {

        //添加
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //添加ids
        criteria.andGoodsIdIn(Arrays.asList(ids));
        //添加状态
        criteria.andStatusEqualTo(status);

        //查询
        List<TbItem> items = itemMapper.selectByExample(example);


        return items;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {

        //设置商品状态 未审核
        TbGoods goods1 = goods.getGoods();
        goods1.setAuditStatus("0");
        //保存商品SPU tb_goods表
        goodsMapper.insert(goods1);

        //保存商品信息描述 tb_goods_desc表
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        //设置goods_id
        goodsDesc.setGoodsId(goods1.getId());
        goodsDescMapper.insert(goodsDesc);

//        int i=1/0;

        //保存商品SKU tb_item表

        saveItem(goods);

    }

    //添加sku
    private void saveItem( Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {


            //获取Sku的信息
            List<TbItem> itemList = goods.getItemList();
            //判断是否有sku信息
            if (itemList != null && itemList.size() > 0) {

                //遍历
                for (TbItem tbItem : itemList) {

                    //设置标题
                    String title = goods.getGoods().getGoodsName();
                    //数据拼接{网络: "移动4G", 机身内存: "64G"}
                    Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
                    Set set = map.keySet();
                    for (Object key : set) {

                        String value = (String) map.get(key);
                        title += "  " + value;

                    }
                    System.out.println(title);

                    //设置标题
                    tbItem.setTitle(title);


                    //设置属性
                    setItemValue(goods, tbItem);


                    //保存SKU
                    itemMapper.insert(tbItem);


                }


            }


        } else {
            //用户未启动规格
            //只生成一个SKU.
            TbItem tbItem = new TbItem();
            //设置标题
            String title = goods.getGoods().getGoodsName();
            tbItem.setTitle(title);

            //设置属性
            setItemValue(goods, tbItem);

            //设置价格
            tbItem.setPrice(goods.getGoods().getPrice());
            //设置库存
            tbItem.setNum(1000);
            //是否启用
            tbItem.setStatus("1");
            //是否默认
            tbItem.setIsDefault("1");

            //保存
            itemMapper.insert(tbItem);


        }


    }




    //抽取公共设置sku的属性
    public void setItemValue(Goods goods, TbItem tbItem) {


        //商品的图片
        List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

        //设置图片，只取图片列表中的第一张图片
        if (list != null && list.size() > 0) {

            //{color: "黑色", url: "http://192.168.188.146/group1/M00/00/00/wKi8kl9LEg2AKZlyAAIbO5uH5xk603.jpg"}
            String url = (String) list.get(0).get("url");
            //设置图片
            tbItem.setImage(url);

        }

        //设置分类
        Long category3Id = goods.getGoods().getCategory3Id();
        tbItem.setCategoryid(category3Id);

        //设置状态
        tbItem.setStatus("1");
        //创建时间
        tbItem.setCreateTime(new Date());
        //修改时间
        tbItem.setUpdateTime(new Date());

        //设置SPUid
        Long goodsId = goods.getGoods().getId();
        tbItem.setGoodsId(goodsId);

        //设置商家id
        String sellerId = goods.getGoods().getSellerId();
        tbItem.setSellerId(sellerId);

        //设置分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(category3Id);

        tbItem.setCategory(itemCat.getName());
        //设置品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());

        //设置商家名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(sellerId);
        tbItem.setSeller(tbSeller.getNickName());

    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {

        //修改SPU tb_goods
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //修改 tb_goods_desc
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

        //修改SKU good_id
        TbItemExample example=new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());

        //删除SKU
        itemMapper.deleteByExample(example);


        //添加SKU
        saveItem(goods);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {

        //查询tb_goods表
        TbGoods tbgoods = goodsMapper.selectByPrimaryKey(id);
        //插叙tb-goods_desc表
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //查询tb_item
        TbItemExample example=new TbItemExample();
        //添加条件
        example.createCriteria().andGoodsIdEqualTo(id);
        //查询
        List<TbItem> itemList = itemMapper.selectByExample(example);


        //封装包装类
        Goods goods=new Goods();
        goods.setGoods(tbgoods);
        goods.setGoodsDesc(goodsDesc);
        goods.setItemList(itemList);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //根据id查询goods
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //修改is_delete 为1表示
            tbGoods.setIsDelete("1");
            //保存
            goodsMapper.updateByPrimaryKey(tbGoods);


        }

        //修改SKU的状态为禁用
        TbItemExample example=new TbItemExample();
        //添加条件
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        //修改
        List<TbItem> items = itemMapper.selectByExample(example);
        for (TbItem item : items) {
            //修改状态
            item.setStatus("0");
            //修改
            itemMapper.updateByPrimaryKey(item);


        }


    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //修改模糊查询为精确查询
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                //此处状态是精确查询
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
            //添加条件，排除已经逻辑删除的商品 is_delete为null表示没有删除
            criteria.andIsDeleteIsNull();

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {

        //修改tb_goos
        for (Long id : ids) {
            //查询
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //设置状态
            tbGoods.setAuditStatus(status);
            //修改
            goodsMapper.updateByPrimaryKey(tbGoods);

             //修改tb_item

            TbItemExample example =new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(id);
            //查询
            List<TbItem> tbItems = itemMapper.selectByExample(example);
            //遍历
            for (TbItem tbItem : tbItems) {

                //修改状态
                tbItem.setStatus(status);
                //更新
                itemMapper.updateByPrimaryKey(tbItem);
            }


        }





    }

}
