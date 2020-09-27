package com.offcn.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;


/**
 * @author ：yz
 * @date ：Created in 2020/9/14 11:24
 * @version: 1.0
 */
@RestController
public class TestController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination topicPageDestination;


    @RequestMapping("/smsSender")
    public String smsSender(String mobile,String param){

    //发送短信消息
        jmsTemplate.send(topicPageDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //获取map类型消息
                MapMessage mapMessage = session.createMapMessage();
                //添加手机号
                mapMessage.setString("mobile",mobile);
                //添加验证码
                mapMessage.setString("param",param);

                return mapMessage;
            }
        });


        return "send OK";


    }

}
