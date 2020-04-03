package com.iyangcong.reader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/15.
 */
/*
个人主页个人信息
 */
public class MineBasicInfo {
    /*
    书摘数
    */
    private int bookmarkNum;
    /*
    购买图书数
     */
    private int buyBookNum;
    /*
    关注我
     */
    private int caremeNum;
    /*
    积分
     */
    private int coin;
    /*
    收藏图书数
     */
    private int collectBookNum;
    /*
    评论数
     */
    private int  commentsNum;
    /*
    我关注
     */
    private int mecareNum;
    /*
    姓名
     */
    private String   name;
    /*
    头像图片
     */
    private String  photo;
    /*
    读后感数量
     */
    private int reviewsNum;
    /*
    性别
     */
    private int sex;
    /*
    阅读兴趣
     */
    private ArrayList<String> userReadInterst;
    /*
    个人签名
     */
    private String userdesc;
    /*
    是否关注(0按钮不使能 1没有关注 2已经关注)
     */
    private int isCared;

    private String coinDegree;

    private int userType;

    public int getUserType(){return userType;}

    public void setUserType(int userType){this.userType=userType;}

    public int getIsCared() {
        return isCared;
    }

    public void setIsCared(int isCared) {
        this.isCared = isCared;
    }

    public ArrayList<String> getUserReadInterst() {
        return userReadInterst;
    }

    public void setUserReadInterst(ArrayList<String> userReadInterst) {
        this.userReadInterst = userReadInterst;
    }



    public int getBookmarkNum() {
        return bookmarkNum;
    }

    public void setBookmarkNum(int bookmarkNum) {
        this.bookmarkNum = bookmarkNum;
    }

    public int getBuyBookNum() {
        return buyBookNum;
    }

    public void setBuyBookNum(int buyBookNum) {
        this.buyBookNum = buyBookNum;
    }

    public int getCaremeNum() {
        return caremeNum;
    }

    public void setCaremeNum(int caremeNum) {
        this.caremeNum = caremeNum;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getCollectBookNum() {
        return collectBookNum;
    }

    public void setCollectBookNum(int collectBookNum) {
        this.collectBookNum = collectBookNum;
    }

    public int getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(int commentsNum) {
        this.commentsNum = commentsNum;
    }

    public int getMecareNum() {
        return mecareNum;
    }

    public void setMecareNum(int mecareNum) {
        this.mecareNum = mecareNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getReviewsNum() {
        return reviewsNum;
    }

    public void setReviewsNum(int reviewsNum) {
        this.reviewsNum = reviewsNum;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCoinDegree() {
        return coinDegree;
    }

    public void setCoinDegree(String coinDegree) {
        this.coinDegree = coinDegree;
    }

    public String getUserdesc() {
        return userdesc;
    }

    public void setUserdesc(String userdesc) {
        this.userdesc = userdesc;
    }
}
