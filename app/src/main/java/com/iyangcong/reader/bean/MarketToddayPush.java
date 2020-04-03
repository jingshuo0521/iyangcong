package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/9.
 */

import java.io.Serializable;

/**
 * 每日推荐书籍
 */
public class MarketToddayPush implements Serializable {
    /**
     * 每日推荐书籍标题
     */
    private String  title;
    /**
     * 每日推荐书籍id
     */
    private String  bookid;
    /**
     * 每日推荐书籍描述
     */
    private String content;
    /**
     * 每日推荐书籍图片url
     */
    private String bookPhoto;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String  getBookid() {
        return bookid;
    }

    public void setBookid(String  bookid) {
        this.bookid = bookid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBookPhoto() {
        return bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }
}
