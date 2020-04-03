package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-03-13.
 * Person：一个简单的类，类中只有两个字段，一个表示id,另一个表示名称；
 */

public class Person {
    private int userId;
    private String userName;
    private String userImage;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
