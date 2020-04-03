package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/4/10.
 */

public class IYangCongBind {
    private String name;
    private int image;
    private String user;
    private  boolean isbind;

    public boolean getisbind() {
        return isbind;
    }

    public void setIsbind(boolean isbind) {
        this.isbind = isbind;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public IYangCongBind(int image, boolean isbind,String user,String name) {
        this.image = image;
        this.user = user;
        this.isbind = isbind;
        this.name = name;
    }
}
