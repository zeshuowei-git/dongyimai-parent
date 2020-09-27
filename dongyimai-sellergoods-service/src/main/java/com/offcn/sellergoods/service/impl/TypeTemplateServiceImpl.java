package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.pojo.TbTypeTemplateExample;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类型模板服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据模板id查询规格信息
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findSpecList(Long id) {

        //根据模板id查询模板对象
        TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
        //获取规格信息
        String specIds = template.getSpecIds();
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]

        //map: {"id":27,"text":"网络",options:[]}
        List<Map> list = JSON.parseArray(specIds, Map.class);

        if (list != null && list.size() > 0) {

            for (Map map : list) {

                //获取id
                Long specId = new Long((Integer) map.get("id"));

                //获取规格id，根据规格id查询规格选项信息
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(specId);

                List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);


                map.put("options", tbSpecificationOptions);

            }


        }


        return list;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    //缓存模板数据到redis
    private void saveToReis() {
        //查询所有的模板
        List<TbTypeTemplate> templateList = findAll();
        //遍历
        for (TbTypeTemplate template : templateList) {

            //获取品牌列表
            String brandIds = template.getBrandIds();
            //JSON转换
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);
            //缓冲品牌
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);

            System.out.println("品牌列表缓存成功");

            //根据模板id获取规格和规格选项
            List<Map> specList = findSpecList(template.getId());

            //缓存规格列表
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            System.out.println("规格列表缓存成功");


        }



    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {


        /*缓存数据模板数据开始*/
        saveToReis();
        /*缓存数据模板数据结束*/
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

}
