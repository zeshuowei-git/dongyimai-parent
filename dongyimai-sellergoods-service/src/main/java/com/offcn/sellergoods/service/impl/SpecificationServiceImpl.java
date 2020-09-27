package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Specification;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 规格服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    /**
     * 返回规格选项列表
     *
     * @return
     */
    @Override
    public List<Map> selectOptions() {

        return specificationMapper.selectOptions();
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {


        //新增规格
        specificationMapper.insert(specification.getSpecification());

        //新增规格选项
        for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {

            //为规格选项添加规格的id
            specificationOption.setSpecId(specification.getSpecification().getId());

            //新增规格选项
            specificationOptionMapper.insert(specificationOption);

        }


    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {


         //修改规格
        specificationMapper.updateByPrimaryKey(specification.getSpecification());

         //修改规格选项

        //1.先删除
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());
        //删除
        specificationOptionMapper.deleteByExample(example);


        //2.在添加

        for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {

            //添加spec_id外键值
            specificationOption.setSpecId(specification.getSpecification().getId());

            //添加规格选项
            specificationOptionMapper.insert(specificationOption);

        }


    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {

        //查询规格数据
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        //查询规格选项数据
        //创建规格选项条件对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //添加条件
        criteria.andSpecIdEqualTo(id);
        //根据 条件查询
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);

        //封装数据
        Specification specification=new Specification();
        //添加规格对象
        specification.setSpecification(tbSpecification);
        //添加规格选项集合
        specification.setSpecificationOptionList(tbSpecificationOptions);

        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {


        for (Long id : ids) {
            //删除规格表
            specificationMapper.deleteByPrimaryKey(id);
            //删除规格选项
            //添加删除条件
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            //添加条件
             example.createCriteria().andSpecIdEqualTo(id);

            specificationOptionMapper.deleteByExample(example);




        }



    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
