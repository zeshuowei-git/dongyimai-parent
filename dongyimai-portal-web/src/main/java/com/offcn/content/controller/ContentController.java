package com.offcn.content.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：yz
 * @date ：Created in 2020/9/3 11:46
 * @version: 1.0
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByContenCategoryId")
    public List<TbContent> findByContenCategoryId(Long categoryId){


        return contentService.findByContentCategoryId(categoryId);
    }


}
