package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */

public class MarketBookListItem implements Serializable {
    @SerializedName(value = "bookIntroduction",alternate ={"intro_zh","guide"} )
    public String bookIntroduction;
    /*
    bookkind 0:专题图 1:图书 2:头图
     */
    public int bookKind = 1;
    @SerializedName(value = "bookTypeList",alternate = "category")
    public List<String> bookTypeList;
    @SerializedName(value = "bookImageUrl",alternate = {"image","monthlyPaymentCover"})
    public String bookImageUrl;
    @SerializedName(value = "bookRating",alternate = "grade")
    public int bookRating;
    @SerializedName(value = "bookLanguage")
    public String bookLanguage;
    public List<String> language;
    /**
     * 原价
     */
    @SerializedName(value = "bookPrice",alternate = {"originalPrice"})
    public double bookPrice;
    @SerializedName(value = "bookAuthor",alternate = "author_zh")
    public String bookAuthor;
    @SerializedName(value = "bookName",alternate = "title_zh")
    public String bookName;
    @SerializedName(value = "bookNameEn",alternate = "title_en")
    public String bookNameEn;
    @SerializedName(value = "translator")
    public String translator;
    public String bookId;
    public String imgUrl;
    @SerializedName(value = "name",alternate = "monthlyPaymentName")
    public String name;
    @SerializedName(value = "id",alternate = "monthlyPaymentId")
    public String id;
    public String content;
    /**
     * 现价
     */
    @SerializedName(value = "bookSpecialPrice",alternate = {"price"})
    private double bookSpecialPrice;

    public double getBookSpecialPrice() {
        return bookSpecialPrice;
    }

    public void setBookSpecialPrice(double bookSpecialPrice) {
        this.bookSpecialPrice = bookSpecialPrice;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getBookKind() {
        return bookKind;
    }

    public void setBookKind(int bookKind) {
        this.bookKind = bookKind;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public List<String> getBookTypeList() {
        return bookTypeList;
    }

    public void setBookTypeList(List<String> bookTypeList) {
        this.bookTypeList = bookTypeList;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public int getBookRating() {
        return bookRating;
    }

    public void setBookRating(int bookRating) {
        this.bookRating = bookRating;
    }

    public String getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookNameEn() {
        return bookNameEn;
    }

    public void setBookNameEn(String bookNameEn) {
        this.bookNameEn = bookNameEn;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }
}
