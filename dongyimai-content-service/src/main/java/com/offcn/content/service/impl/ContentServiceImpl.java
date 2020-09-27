package com.offcn.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.content.service.ContentService;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 内容服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 根据类目id查询广告
	 *
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findByContentCategoryId(Long categoryId) {


		//尝试用redis缓存中获取
		List<TbContent> content = (List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);

		//判断
		if(content==null||content.size()<=0){

//			从数据库查询
			//创建条件对象
			TbContentExample example=new TbContentExample();
			//添加条件
			example.createCriteria().andCategoryIdEqualTo(categoryId);
			//查询
			content = contentMapper.selectByExample(example);

			//存储数据到缓存
			redisTemplate.boundHashOps("content").put(categoryId,content);

			System.out.println("从数据库中查询");
		}else{

			System.out.println("从缓存中获取的数据");
		}








		return content;





	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		contentMapper.insert(content);

		//清楚缓存
		redisTemplate.boundHashOps("content").rename(content.getCategoryId());




	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		//请求改变前的分类
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		//获取改变前的分类
		Long categoryId = tbContent.getCategoryId();
		//请求改变前的分类
		redisTemplate.boundHashOps("content").rename(categoryId);


		contentMapper.updateByPrimaryKey(content);


		//改变后，判断分类是否进行了改变
		if(categoryId.longValue()!=content.getCategoryId().longValue()){
			//类别 发生了改变

			redisTemplate.boundHashOps("content").rename(content.getCategoryId());



		}




	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
			for(Long id:ids){


				//根据id查询广告对象
				TbContent tbContent = contentMapper.selectByPrimaryKey(id);
				//从广告对象中获取分类id
				Long categoryId = tbContent.getCategoryId();

				//请求数据
				redisTemplate.boundHashOps("content").rename(categoryId);

				//删除
				contentMapper.deleteByPrimaryKey(id);



			}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
