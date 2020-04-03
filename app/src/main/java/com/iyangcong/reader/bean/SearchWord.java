package com.iyangcong.reader.bean;

import java.util.List;

public class SearchWord {
    private String us_phonetic;
    private String phonetic;
    private List<SearchBook> books;
    private String uk_phonetic;
    private String level;
    private String variant;
    private String explans;

    public String getUs_phonetic() {
        return us_phonetic;
    }

    public void setUs_phonetic(String us_phonetic) {
        this.us_phonetic = us_phonetic;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public List<SearchBook> getBooks() {
        return books;
    }

    public void setBooks(List<SearchBook> books) {
        this.books = books;
    }

    public String getUk_phonetic() {
        return uk_phonetic;
    }

    public void setUk_phonetic(String uk_phonetic) {
        this.uk_phonetic = uk_phonetic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getExplans() {
        return explans;
    }

    public void setExplans(String explans) {
        this.explans = explans;
    }
}