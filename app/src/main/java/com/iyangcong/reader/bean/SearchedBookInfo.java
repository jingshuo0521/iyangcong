package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */
public class SearchedBookInfo {
    private int bookId;
    private String author_zh;
    private List<String> category;
    private int grade;
    private String image;
    private String intro_zh;
    private String language;
    private String title_en;
    private String title_zh;
    private String translator;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getAuthor_zh() {
        return author_zh;
    }

    public void setAuthor_zh(String author_zh) {
        this.author_zh = author_zh;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntro_zh() {
        return intro_zh;
    }

    public void setIntro_zh(String intro_zh) {
        this.intro_zh = intro_zh;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getTitle_zh() {
        return title_zh;
    }

    public void setTitle_zh(String title_zh) {
        this.title_zh = title_zh;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }
}
