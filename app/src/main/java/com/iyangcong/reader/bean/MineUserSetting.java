package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/3/22.
 */

public class MineUserSetting {

    /*
    用户类型
     */
    private int userType;
    /*
    个人昵称
     */
    private String userNickname;
    /*
    个人电话
   */
    private String userPhoneNum;
    /*
    个人姓名
    */
    private String userName;
    /*
    个人性别
    */
    private String userSex;
    /*
    个人地区
    */
    private String userRegion;
    /*
    个人简介
    */
    private String userIntroduction;
    /*
    个人帐号
     */
    private String userAccounts;
    /*
    个人头像
     */

    private String userHead;

    public String getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(String userAccounts) {
        this.userAccounts = userAccounts;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserIntroduction() {
        return userIntroduction;
    }

    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(String userRegion) {
        this.userRegion = userRegion;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
