package com.offcn.page.service.impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author ：yz
 * @date ：Created in 2020/9/11 16:18
 * @version: 1.0
 */
public class pageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;


    @Override
    public void onMessage(Message message) {

        try {
            //获取ids
            ObjectMessage objectMessage=(ObjectMessage)message;
            //转换为Long []ids
            Long[] ids=(Long [])objectMessage.getObject();

            //删除
            for (Long goodsId : ids) {

                boolean flag = itemPageService.deletePageHtml(goodsId);
                System.out.println("结果"+flag+"页面已经删除："+goodsId+".html");
            }




        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
