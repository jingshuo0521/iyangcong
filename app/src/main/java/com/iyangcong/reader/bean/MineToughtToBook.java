package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/14.
 */
/*
个人主页读后感
 */
public class MineToughtToBook {
    /*
    图书版本
    */
    private String edition;
    /*
    用户头像
     */
    private String userImageUrl;
    /*
    书的封面
     */
    @SerializedName(value = "bookcover",alternate ={"bookCover"})
    private String bookcover;
    /*
    读后感ID
    */
    @SerializedName(value = "reviewId",alternate ={"reviewsId"})
    private int reviewId;
    /*

     */
    private String guid;
    /*
    读后感内容
     */
    @SerializedName("content")
    private String  content;
    /*
    时间
     */
    private String  createtime;
    /*
    回复数量
    */
    private int messageNum;
    /*
    图书作者
    */
    private String auther;
    /*
     读后感标题
     */
    @SerializedName("title")
    private String title;
    /*
    书ID
    */
    @SerializedName(value = "bookid",alternate ={"bookId"})
    private int bookid;
    /*

     */
    private String TitleEn;
    /*

    */
    private int userId;
    /*
    喜欢数量
     */
    private int reviewsLike;
    /*

    */
    private int grade;
    /*
    用户名
    */
    @SerializedName(value = "userName",alternate ={"authorName"})
    private String  userName;
    /*
     书名
      */
    private String TitleZh;
    /*

     */
    private String PublishHouse;

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getBookcover() {
        return bookcover;
    }

    public void setBookcover(String bookcover) {
        this.bookcover = bookcover;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public String getPublishHouse() {
        return PublishHouse;
    }

    public void setPublishHouse(String publishHouse) {
        PublishHouse = publishHouse;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getReviewsLike() {
        return reviewsLike;
    }

    public void setReviewsLike(int reviewsLike) {
        this.reviewsLike = reviewsLike;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEn() {
        return TitleEn;
    }

    public void setTitleEn(String titleEn) {
        TitleEn = titleEn;
    }

    public String getTitleZh() {
        return TitleZh;
    }

    public void setTitleZh(String titleZh) {
        TitleZh = titleZh;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
