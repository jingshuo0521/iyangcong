package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/4/10.
 */

public class BindSituation {
    /*
    第三方登陆openid
     */
    private String openId;
    /*
    绑定手机号
     */
    private String mobile;
    /*
    用户名
     */
    private String name;
    /*
    绑定类型
    * type = 1 表示微信；
    * type = 2 表示QQ；
    * type = 3 表示微博；
    * type = 4 表示豆瓣；
    */
    private int type;
    /*
    绑定邮箱
     */
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
