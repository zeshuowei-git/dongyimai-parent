package com.offcn.user.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：yz
 * @date ：Created in 2020/9/15 14:50
 * @version: 1.0
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //授权
        List<SimpleGrantedAuthority> list=new ArrayList<>();
        //添加权限
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));



        return new User(username,"",list);
    }
}

