package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/19.
 */

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.iyangcong.reader.ui.dragmerge.model.BookDataGroup;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 书架已添加图书
 */
@DatabaseTable(tableName = "book")
public class ShelfBook extends BaseBook implements Serializable, Comparable<ShelfBook> {

    private static final long serialVersionUID = 8490592264333599695L;
    @DatabaseField(generatedId = true, columnName = "id")
    private int id;
    @DatabaseField(columnName = "userId")
    private long userId;
    /**
     * 图书状态 已读XX%、未读、未下载、正在下载、暂停下载
     */
    @DatabaseField(columnName = "bookState")
    private String bookState;

    /**
     * 属于某分组的名称
     */
    @DatabaseField(columnName = "groupName")
    private String groupName;
    /**
     * 图书下载进度条
     */
    @DatabaseField(columnName = "progress")
    private float progress;
    /**
     * 图书类别
     * 0:免费图书   1:试读图书  2:已购买图书  3:包月已购买图书
     */
    @DatabaseField(columnName = "bookType")
    private int bookType;

    /**
     * 本地图书路径
     */
    @DatabaseField(columnName = "bookPath")
    private String bookPath;

    /**
     * supportLanguage 1:中文 2:英文 3.中/英
     */
    @SerializedName(value = "supportLanguage",alternate = "language")
    @DatabaseField(columnName = "supportLanguage")
    private int supportLanguage;

    /**
     * 最近一次阅读的语言类型   0:没有阅读记录   0:暂无阅读记录 1:中文 2:英文
     */
    @DatabaseField(columnName = "recentReadingLanguageType")
    private int recentReadingLanguageType;
    /**
     * 如果该书籍是包月书籍，则该字段表示包月书籍的到期时间；
     */
    @DatabaseField(columnName = "bookEndTime", dataType = DataType.STRING, defaultValue = "")
    private String endTime;
    private boolean isChecked;
    private BookDataGroup mParent;
    @DatabaseField(columnName = "chapterId", dataType = DataType.STRING, defaultValue = "")
    private String chapterId;

    /**
     * 最近阅读时间
     */
    @DatabaseField(columnName = "timeStamp")
    private long timeStamp;

    @DatabaseField(columnName = "encryVersion",dataType = DataType.INTEGER,defaultValue = "-1")
    private int encryVersion;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getBookState() {
        return bookState;
    }

    public void setBookState(String bookState) {
        this.bookState = bookState;
    }

    public float getDownloadProgress() {
        return progress;
    }

    public void setDownloadProgress(float downloadProgress) {
        this.progress = downloadProgress;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public int getSupportLanguage() {
        return supportLanguage;
    }

    public void setSupportLanguage(int supportLanguage) {
        this.supportLanguage = supportLanguage;
    }

    public int getRecentReadingLanguageType() {
        return recentReadingLanguageType;
    }

    public void setRecentReadingLanguageType(int recentReadingLanguageType) {
        this.recentReadingLanguageType = recentReadingLanguageType;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public BookDataGroup getParent() {
        return mParent;
    }

    public void setParent(BookDataGroup parent) {
        mParent = parent;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public String toString() {
        return super.toString() +
                "ShelfBook{" +
                "bookState='" + bookState + '\'' +
                ", downloadProgress=" + progress +
                ", bookType=" + bookType +
                '}';
    }

    @Override
    public int compareTo(@NonNull ShelfBook shelfBook) {
        return (int) (shelfBook.getTimeStamp() - this.getTimeStamp());
    }
}
