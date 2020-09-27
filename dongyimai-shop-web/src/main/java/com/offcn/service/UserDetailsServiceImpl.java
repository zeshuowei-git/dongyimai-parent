package com.offcn.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证类
 * @author ：yz
 * @date ：Created in 2020/8/27 15:02
 * @version: 1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {


      private SellerService sellerService;

        public void setSellerService(SellerService sellerService) {
            this.sellerService = sellerService;
        }

    /**
     *
     * @param username 使用用户登录时输入的用户名
     *                 又是商家表中的登录名sellerId
     *
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //设置权限列表
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //根据用户名查询商家信息
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){
            if(seller.getStatus().equals("1")){

                //已审核
                return new User(seller.getSellerId(),seller.getPassword(),grantedAuthorities);

            }else{
                //未审核
                return null;

            }


        }else{

            return null;
        }

//        return new User(username,"123456",grantedAuthorities);
    }
}
