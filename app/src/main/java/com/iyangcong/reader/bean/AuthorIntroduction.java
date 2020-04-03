package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by lg on 2016/12/24.
 */

public class AuthorIntroduction {
    private int authorID;
    private String name;
    private String introduce;
    private List<BaseBook> bookList;
    private String headImgUrl;

    public String getName() {
        return name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public List<BaseBook> getBookList() {
        return bookList;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setBookList(List<BaseBook> bookList) {
        this.bookList = bookList;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getAuthorID() {
        return authorID;
    }
}
