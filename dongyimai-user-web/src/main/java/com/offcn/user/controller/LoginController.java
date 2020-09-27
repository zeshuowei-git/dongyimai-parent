package com.offcn.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：yz
 * @date ：Created in 2020/9/15 14:58
 * @version: 1.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {


    @RequestMapping("/name")
    public Map getLoginName(){
        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //存储登录名
        Map map=new HashMap();
        map.put("loginName",name);

        //返回登录名
        return map;




    }

}
