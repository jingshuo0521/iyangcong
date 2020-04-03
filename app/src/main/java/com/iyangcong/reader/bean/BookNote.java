package com.iyangcong.reader.bean;

/**
 * Created by DarkFlameMaster on 2017/5/5.
 */

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 图书笔记
 */
@DatabaseTable(tableName = "booknote")
public class BookNote implements Serializable {
    @DatabaseField(generatedId = true, columnName = "id")
    private int id;
    @DatabaseField(columnName = "commentId")
    @SerializedName("commentid")
    private int commentId;// 作为笔记的m_id
    @DatabaseField(columnName = "segmentNum")
    @SerializedName("segmentnum")
    private int segmentNum;
    @DatabaseField(columnName = "bookId")
    @SerializedName("bookid")
    private int bookId;
    @DatabaseField(columnName = "language")
    @SerializedName("language")
    private int language;
    @DatabaseField(columnName = "note")
    @SerializedName("note")
    private String note;
    @DatabaseField(columnName = "noteComment",dataType = DataType.STRING,defaultValue = "")
    @SerializedName("noteComment")
    private String noteComment;
    @DatabaseField(columnName = "stuCommentsId",dataType = DataType.INTEGER)
    @SerializedName("stuCommentsId")
    private int stuCommentsId;// 作为笔记id（私密笔记没有此项）
    @DatabaseField(columnName = "chapterId")
    @SerializedName("chapterId")
    private int chapterId;
    @DatabaseField(columnName = "iscommon")
    @SerializedName("iscommon")
    private int iscommon; // 1:公开 0:私密

    public int getIscommon() {
        return iscommon;
    }

    public void setIscommon(int iscommon) {
        this.iscommon = iscommon;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
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

    public int getSegmentNum() {
        return segmentNum;
    }

    public void setSegmentNum(int segmentNum) {
        this.segmentNum = segmentNum;
    }

    public String getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(String noteComment) {
        this.noteComment = noteComment;
    }

    public int getStuCommentsId() {
        return stuCommentsId;
    }

    public void setStuCommentsId(int stuCommentsId) {
        this.stuCommentsId = stuCommentsId;
    }
}
