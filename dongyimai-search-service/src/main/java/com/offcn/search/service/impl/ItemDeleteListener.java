package com.offcn.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author ：yz
 * @date ：Created in 2020/9/11 15:20
 * @version: 1.0
 */
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSarchServiceImpl itemSarchService;


    @Override
    public void onMessage(Message message) {
        try {
            //转换消息
            ObjectMessage objectMessage=(ObjectMessage)message;
            //转换成Long []
            Long[] ids =(Long[]) objectMessage.getObject();

            //删除solr库中数据
            itemSarchService.deleteByGoodsIds(Arrays.asList(ids));

            System.out.println("移除成功"+Arrays.toString(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
