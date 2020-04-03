package com.iyangcong.reader.bean;
/**
 * author:DarkFlameMaster </br>
 * time:2019/6/13 15:56 </br>
 * desc:微信支付结果调用bean </br>
 */
public class WeChatPayResult {
    public int errCode;
    public String errStr;

    public WeChatPayResult(int errCode, String errStr) {
        this.errCode = errCode;
        this.errStr = errStr;
    }
}
