package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/3/14.
 */

/**
 * 个人主页动态
 */
public class MineDynamic {
    /*
    用户名
     */
    private String userName;
    /*
    用户id
     */
    private int userId;
    /*
    时间戳
     */
    private String time;
    /*
    动态名称
     */
    private String stateName;
    /*
    动态id
     */
    private int stateId;
    /*
    读后感、评论
     */
    private String resourceName;
    /*
    书名
     */
    private String  bookName;
    /*
    书id
     */
    private int bookId;
    /*
    喜欢发表
     */
    private String action;
    /*
    动态总数
     */
    private int totalNum;

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
