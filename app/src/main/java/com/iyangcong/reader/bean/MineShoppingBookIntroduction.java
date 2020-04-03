package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by DarkFlameMaster on 2016/12/24 0024.
 */

public class MineShoppingBookIntroduction {

    /*
    是否被选中
     */
    private boolean choose;

    private String bookImageUrl;

    private List<String> bookLanguage;

    private String bookAuthor;

    private String bookName;

    private int bookId;

    private MineShoppingPrice bookPrice;

    private double presentPrice;

    public double getPresentPrice() {
        return presentPrice;
    }

    public void setPresentPrice(double presentPrice) {
        this.presentPrice = presentPrice;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public List<String> getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(List<String> bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public MineShoppingPrice getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(MineShoppingPrice bookPrice) {
        this.bookPrice = bookPrice;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }


    public boolean isChoose() {
        return choose;
    }
}
