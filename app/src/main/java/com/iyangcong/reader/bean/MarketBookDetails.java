package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */

public class MarketBookDetails {
    private int bookKind;
    private List<String> bookTypeList;
    private String bookImageUrl;
    private String sound;
    private String translator;
    private int bookRating = 5;
    private List<MarketBookDetailsSameBooks> sameoffer;
    private String bookLanguage;
    private String bookAuthor;
    private String authorSimpleIntroduction;
    private String bookName;
    private String version;
    private int wordNum;
    private String upTime;
    private String bookNumber;
    private String author_en;
    private int bookId;
    private int gradelevel;
    private String bookIntroduction_en;
    private String bookIntroduction;
    private float normal_price;
    private List<DiscoverReviews> reviews;
    private String title_en;

    /**
     * 图书支持终端类型 1:web  2:ios  3:android
     */
    private String terminal;

    /**
     * 图书状态 1.上架  2.未上架  3.政治敏感  4.内容错误  5.版权问题
     * 如果status：
     * 1正常
     * 4,5还可以下载阅读
     * 2,3什么按钮都没有
     */
    private int status;

    private double original_price;
    private double free_status;
    private double virtual_price;
    private double original_price_name;
    private double virtual_status;
    private double price;
    private double special_price;
    private double price_type;
    private double special_status;
    private int startChapterId;

    public int getStartChapterId() {
        return startChapterId;
    }

    public int getBookKind() {
        return bookKind;
    }

    public void setBookKind(int bookKind) {
        this.bookKind = bookKind;
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

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public int getBookRating() {
        return bookRating;
    }

    public void setBookRating(int bookRating) {
        this.bookRating = bookRating;
    }

    public List<MarketBookDetailsSameBooks> getSameoffer() {
        return sameoffer;
    }

    public void setSameoffer(List<MarketBookDetailsSameBooks> sameoffer) {
        this.sameoffer = sameoffer;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getAuthorSimpleIntroduction() {
        return authorSimpleIntroduction;
    }

    public void setAuthorSimpleIntroduction(String authorSimpleIntroduction) {
        this.authorSimpleIntroduction = authorSimpleIntroduction;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor_en() {
        return author_en;
    }

    public void setAuthor_en(String author_en) {
        this.author_en = author_en;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getGradelevel() {
        return gradelevel;
    }

    public void setGradelevel(int gradelevel) {
        this.gradelevel = gradelevel;
    }

    public String getBookIntroduction_en() {
        return bookIntroduction_en;
    }

    public void setBookIntroduction_en(String bookIntroduction_en) {
        this.bookIntroduction_en = bookIntroduction_en;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public float getNormal_price() {
        return normal_price;
    }

    public void setNormal_price(float normal_price) {
        this.normal_price = normal_price;
    }

    public List<DiscoverReviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<DiscoverReviews> reviews) {
        this.reviews = reviews;
    }

    public double getSpecial_price() {
        return special_price;
    }

    public void setSpecial_price(double special_price) {
        this.special_price = special_price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getFree_status() {
        return free_status;
    }

    public void setFree_status(double free_status) {
        this.free_status = free_status;
    }

    public double getVirtual_price() {
        return virtual_price;
    }

    public void setVirtual_price(double virtual_price) {
        this.virtual_price = virtual_price;
    }

    public double getOriginal_price_name() {
        return original_price_name;
    }

    public void setOriginal_price_name(double original_price_name) {
        this.original_price_name = original_price_name;
    }

    public double getVirtual_status() {
        return virtual_status;
    }

    public void setVirtual_status(double virtual_status) {
        this.virtual_status = virtual_status;
    }

    public double getPrice_type() {
        return price_type;
    }

    public void setPrice_type(double price_type) {
        this.price_type = price_type;
    }

    public double getSpecial_status() {
        return special_status;
    }

    public void setSpecial_status(double special_status) {
        this.special_status = special_status;
    }

    public int getWordNum() {
        return wordNum;
    }

    public void setWordNum(int wordNum) {
        this.wordNum = wordNum;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ", original_price=" + original_price +
                ", free_status=" + free_status +
                ", virtual_price=" + virtual_price +
                ", original_price_name=" + original_price_name +
                ", virtual_status=" + virtual_status +
                ", price=" + price +
                ", special_price=" + special_price +
                ", price_type=" + price_type +
                ", special_status=" + special_status +
                '}';
    }
}
