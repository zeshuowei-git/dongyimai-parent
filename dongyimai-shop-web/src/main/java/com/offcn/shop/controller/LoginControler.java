package com.offcn.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：yz
 * @date ：Created in 2020/8/27 10:40
 * @version: 1.0
 */
@RequestMapping("/login")
@RestController
public class LoginControler {



    @RequestMapping("/loginName")
    public Map getLoingName(){

        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //封装
        Map map=new HashMap();
        map.put("loginName",name);

        return map;


    }

}
