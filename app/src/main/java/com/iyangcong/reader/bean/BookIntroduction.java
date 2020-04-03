package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author ljw
 *         书籍介绍
 *         2016/12/7.
 */

public class BookIntroduction extends BaseBook {
    /**
     * 识别当前为图片或者图书字段    0:图片    1:图书
     */
    private int bookKind;
    /**
     * 图书语种
     */
    @SerializedName(value = "bookLanguage",alternate = "language")
    private List<String> bookLanguage;
    /**
     * 图书推荐度
     */
    @SerializedName(value = "bookRating",alternate = "grade")
    private float bookRating;
    /**
     * 图书类别
     */
    @SerializedName(value = "bookTypeList",alternate = "category")
    private List<String> bookTypeList;
    /*
    * 图书类别（0:免费图书 1:试读图书 2:已购买图书）
    */
    private int bookType = 10 ;
    /**
     * 是否被选中
     */
    private boolean isChecked;

    /***
     * 是否可见(在搜索的时候用)
     */
    private boolean isVisible;
    /**
     * 书籍的英文名称
     */
    @SerializedName(value = "bookNameEn",alternate = "title_en")
    private String bookNameEn;

    /**
     * 翻译
     */
    @SerializedName(value = "translators",alternate = "translator")
    private String translators;

    @Override
    public boolean equals(Object o) {
        return o != null && ((BookIntroduction)o).getBookId()==getBookId();
    }

    public int getBookKind() {
        return bookKind;
    }

    public void setBookKind(int bookKind) {
        this.bookKind = bookKind;
    }

    public List<String> getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(List<String> bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public float getBookRating() {
        return bookRating;
    }

    public void setBookRating(float bookRating) {
        this.bookRating = bookRating;
    }

    public List<String> getBookTypeList() {
        return bookTypeList;
    }

    public void setBookTypeList(List<String> bookTypeList) {
        this.bookTypeList = bookTypeList;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getBookNameEn() {
        return bookNameEn;
    }

    public void setBookNameEn(String bookNameEn) {
        this.bookNameEn = bookNameEn;
    }

    public String getTranslators() {
        return translators;
    }

    public void setTranslators(String translators) {
        this.translators = translators;
    }

    @Override
    public String toString() {
        return "BookIntroduction{" +
                "bookKind=" + bookKind +
                ", bookLanguage=" + bookLanguage +
                ", bookRating=" + bookRating +
                ", bookTypeList=" + bookTypeList +
                ", bookType=" + bookType +
                ", isChecked=" + isChecked +
                ", isVisible=" + isVisible +
                ", bookNameEn='" + bookNameEn + '\'' +
                ", translators='" + translators + '\'' +
                '}';
    }
}
