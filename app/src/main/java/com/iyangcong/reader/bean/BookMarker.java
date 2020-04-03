package com.iyangcong.reader.bean;

/**
 * Created by DarkFlameMaster on 2017/5/2.
 */

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 书架已添加图书
 */
@DatabaseTable(tableName = "bookmarker")
public class BookMarker implements Serializable{
    @DatabaseField(generatedId = true, columnName = "id")
    private int id;
    @DatabaseField(columnName = "bookmarkerId")
    @SerializedName("bookmarkerid")
    private int bookmarkerId;
    @DatabaseField(columnName = "segmentNum")
    @SerializedName("segmentnum")
    private int segmentNum;
    @DatabaseField(columnName = "bookId")
    @SerializedName("bookid")
    private int bookId;
    @DatabaseField(columnName = "language")
    @SerializedName("language")
    private int language;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSegmentNum() {
        return segmentNum;
    }

    public void setSegmentNum(int segmentNum) {
        this.segmentNum = segmentNum;
    }

    public int getBookmarkerId() {
        return bookmarkerId;
    }

    public void setBookmarkerId(int bookmarkerId) {
        this.bookmarkerId = bookmarkerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
