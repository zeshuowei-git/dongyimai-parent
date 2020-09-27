package com.offcn.page.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author ：yz
 * @date ：Created in 2020/9/11 16:02
 * @version: 1.0
 */
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageServiceImpl itemPageService;

    @Override
    public void onMessage(Message message) {

        try {
            //转换
            TextMessage textMessage=(TextMessage)message;
            //转换成 Long goodsId
            Long goodsId = new Long(textMessage.getText());
            //页面生成
            itemPageService.getItemHtml(goodsId);
            System.out.println("详情页生成="+goodsId+".html");


        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
