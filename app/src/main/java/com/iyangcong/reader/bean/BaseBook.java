package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author ljw
 *         书的基类
 *         2016/12/19.
 */
public class BaseBook {
    /**
     * 书id
     */
    @SerializedName(value = "bookId",alternate = "bookid")
    @DatabaseField(columnName = "bookId")
    private long bookId;
    /**
     * 书名
     */
    @SerializedName(value = "bookName",alternate = "title_zh")
    @DatabaseField(columnName = "bookName")
    private String bookName;
    /**
     * 图书作者
     */
    @SerializedName(value = "bookAuthor",alternate = "author_zh")
    @DatabaseField(columnName = "bookAuthor")
    private String bookAuthor;
    /**
     * 图书介绍
     */
    @SerializedName(value = "bookIntroduction",alternate = "intro_zh")
    @DatabaseField(columnName = "bookIntroduction")
    private String bookIntroduction;
    /**
     * 图书imageUrl
     */
    @SerializedName(value = "bookImageUrl",alternate = {"bookImgUrl","image","cover"})
    @DatabaseField(columnName = "bookImageUrl")
    private String bookImageUrl;
    /*
   图书价格
    */
    @DatabaseField(columnName = "bookPrice")
    private double bookPrice;

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    @Override
    public String toString() {
        return "BaseBook{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", bookIntroduction='" + bookIntroduction + '\'' +
                '}';
    }
}
