package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/5/4.
 */

public class MsgBookInfo {
    BookInfo bookInfoEn ;
    BookInfo bookInfoZh ;
    int bookId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public com.iyangcong.reader.bean.BookInfo getBookInfoEn() {
        return bookInfoEn;
    }

    public void setBookInfoEn(com.iyangcong.reader.bean.BookInfo bookInfoEn) {
        this.bookInfoEn = bookInfoEn;
    }

    public com.iyangcong.reader.bean.BookInfo getBookInfoZh() {
        return bookInfoZh;
    }

    public void setBookInfoZh(com.iyangcong.reader.bean.BookInfo bookInfoZh) {
        this.bookInfoZh = bookInfoZh;
    }
}
