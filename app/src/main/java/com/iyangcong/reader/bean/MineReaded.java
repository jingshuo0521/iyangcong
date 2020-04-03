package com.iyangcong.reader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/15.
 */
/*
个人主页读过
 */
public class MineReaded {
    /*
    作者
     */
    private String   bookAuthor;
    /*
    出版
     */
    private String school;
    /*
    英文标题
     */
    private String title_en;
    /*
    版本
     */
    private ArrayList<String> version;
    /*
    书id
     */
    private int bookid;
    /*
    中文标题
     */
    private String title_zh;
    /*
    书封面
     */
    private String bookImageUrl;

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }


    public ArrayList<String> getVersion() {
        return version;
    }

    public void setVersion(ArrayList<String> version) {
        this.version = version;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getTitle_zh() {
        return title_zh;
    }

    public void setTitle_zh(String title_zh) {
        this.title_zh = title_zh;
    }
}
