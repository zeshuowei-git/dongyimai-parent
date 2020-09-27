package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbTypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * 类型模板服务层接口
 * @author Administrator
 *
 */
public interface TypeTemplateService {

	/**
	 * 根据模板id查询规格信息
	 * @param id
	 * @return
	 */
	public List<Map> findSpecList(Long id);


	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbTypeTemplate> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbTypeTemplate type_template);


	/**
	 * 修改
	 */
	public void update(TbTypeTemplate type_template);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbTypeTemplate findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbTypeTemplate type_template, int pageNum, int pageSize);
	
}
