package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by DarkFlameMaster on 2017/5/3.
 */
/*
书架中书籍章节、段落信息
 */
@DatabaseTable(tableName = "bookinfo")
public class BookInfo implements Serializable{
    @DatabaseField(generatedId = true, columnName = "id")
    private int id;
    @DatabaseField(columnName = "bookId")
    @SerializedName("bookid")
    private long bookId;
    @DatabaseField(columnName = "language")
    @SerializedName("language")
    private int language;
    @DatabaseField(columnName = "chapterId")
    @SerializedName("chapterid")
    private String chapterId;
    @DatabaseField(columnName = "chapterName")
    @SerializedName("chaptername")
    private String englishChapterName;
    @DatabaseField(columnName = "segmentId")
    @SerializedName("segmentid")
    private int segmentId;
    @DatabaseField(columnName = "encryVersion",dataType = DataType.INTEGER,defaultValue = "-1")
    private int encryVersion;
    @SerializedName("chineseChapterName")
    @DatabaseField(columnName = "chineseChapterName",dataType =DataType.STRING,defaultValue = "")
    private String chineseChapterName;
    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public String getEnglishChapterName() {
        return englishChapterName;
    }

    public void setEnglishChapterName(String englishChapterName) {
        this.englishChapterName = englishChapterName;
    }

    public String getChineseChapterName() {
        return chineseChapterName;
    }

    public void setChineseChapterName(String chineseChapterName) {
        this.chineseChapterName = chineseChapterName;
    }
}
