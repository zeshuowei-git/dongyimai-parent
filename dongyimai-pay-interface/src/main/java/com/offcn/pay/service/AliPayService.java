package com.offcn.pay.service;
import java.util.Map;
/**
 * 支付宝支付接口
 * @author Administrator
 *
 */
public interface AliPayService {

    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    public Map queryPayStatus(String out_trade_no);
}