package com.offcn.sms;

import com.offcn.sms.util.SmsUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author ：yz
 * @date ：Created in 2020/9/14 10:34
 * @version: 1.0
 */
public class SmsListener  implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {
        try {
            //消息类型转换
            MapMessage mapMessage=(MapMessage)message;
            //获取手机号
            String mobile = mapMessage.getString("mobile");
            //获取验证码
            String param = mapMessage.getString("param");
            //发送短信
            HttpResponse httpResponse = smsUtil.sendSms(mobile, param);
            //打印信息
            System.out.println("手机号为:"+mobile+",验证码："+param);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
