package com.iyangcong.reader.bean;
/**
 * author:DarkFlameMaster </br>
 * time:2019/6/13 15:30 </br>
 * desc:微信支付获取预支付订单信息的类文件 </br>
 */
public class WechatpayOrderInfo {
    //订单信息参数
    private String resultString;
    //订单号
    private String outTradeNo;

    public String getResultString() {
        return resultString;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }
}
