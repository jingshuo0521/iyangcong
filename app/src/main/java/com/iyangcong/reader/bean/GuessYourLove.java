package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2017/3/20.
 */

public class GuessYourLove {

    private String cover;
    private String title_zh;
    private int bookid;

    public String getBookImgUrl() {
        return cover;
    }

    public void setBookImgUrl(String bookImgUrl) {
        this.cover = bookImgUrl;
    }

    public String getBookName() {
        return title_zh;
    }

    public void setBookName(String bookName) {
        this.title_zh = bookName;
    }

    public int getBookId() {
        return bookid;
    }

    public void setBookId(int bookId) {
        this.bookid = bookId;
    }
}
