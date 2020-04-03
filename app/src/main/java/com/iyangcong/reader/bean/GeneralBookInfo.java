package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DarkFlameMaster on 2017/5/15.
 */

public class GeneralBookInfo implements Serializable {
    public double virtual_price;
    public double original_price;
    public double original_price_name;
    public int bookId;
    public int special_status;
    private double special_price;
    private int price_type;
    public int virtual_status;
    public int category;
    @SerializedName(value = "bookAuthor",alternate = "author_zh")
    public String bookAuthor;
    public double price;
    @SerializedName(value = "bookImageUrl",alternate = "image")
    public String bookImageUrl;
    public int free_status;
    @SerializedName(value = "bookIntroduction",alternate ={"intro_zh","guide"} )
    public String bookIntroduction;
    @SerializedName(value = "bookRating",alternate = "grade")
    public String bookRating;
    @SerializedName(value = "bookLanguage")
    public String bookLanguage;
    /*
   bookkind 0:专题图 1:图书 2:头图
    */
    public int bookKind = 1;
    @SerializedName(value = "bookName",alternate = "title_zh")
    public String bookName;
    /**
     * 标签；
     */
    @SerializedName("tags")
    private List<String> tags;

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

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public int getBookKind() {
        return bookKind;
    }

    public void setBookKind(int bookKind) {
        this.bookKind = bookKind;
    }

    public String getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookRating() {
        return bookRating;
    }

    public void setBookRating(String bookRating) {
        this.bookRating = bookRating;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getFree_status() {
        return free_status;
    }

    public void setFree_status(int free_status) {
        this.free_status = free_status;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getOriginal_price_name() {
        return original_price_name;
    }

    public void setOriginal_price_name(double original_price_name) {
        this.original_price_name = original_price_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
    }

    public double getSpecial_price() {
        return special_price;
    }

    public void setSpecial_price(double special_price) {
        this.special_price = special_price;
    }

    public int getSpecial_status() {
        return special_status;
    }

    public void setSpecial_status(int special_status) {
        this.special_status = special_status;
    }

    public double getVirtual_price() {
        return virtual_price;
    }

    public void setVirtual_price(double virtual_price) {
        this.virtual_price = virtual_price;
    }

    public int getVirtual_status() {
        return virtual_status;
    }

    public void setVirtual_status(int virtual_status) {
        this.virtual_status = virtual_status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
