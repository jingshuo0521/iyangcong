package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by WuZepeng on 2017-05-22.
 */

public class ReadingRecorder implements Serializable {

    private static final long serialVersionUID = -2912633824871228823L;
    /**
     * 图书id
     */
    @SerializedName("bookid")
    private int bookId;
    /**
     * 章节id
     */
    @SerializedName("chapterid")
    private int chapterId;
    /**
     * 语言类型		0双语 1中文 2英文
     */
    @SerializedName("languagetype")
    private int languageType;
    /**
     * 阅读进度
     */
    private float percent;
    /**
     * 资源类型	 1图书；10轻杂志
     */
    @SerializedName("resourcetype")
    private int resourceType;
    /**
     * 段落id
     */
    @SerializedName("segmentid")
    private int segmentId;
    /**
     * 最近一次记录阅读进度的时间
     */
    @SerializedName("date")
    private Long leaveTime;
    /**
     * 段落在本书中实际的id
     */
    private int segmentIndex;
    /**
     * ios用到的偏移量
     */
    private int endOffset;
    /**
     * ios用到的偏移量
     */
    private int startOffset;

    private String segmentStr;

    private int semesterId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getLanguageType() {
        return languageType;
    }

    public void setLanguageType(int languageType) {
        this.languageType = languageType;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public String getSegmentStr() {
        return segmentStr;
    }

    public void setSegmentStr(String segmentStr) {
        this.segmentStr = segmentStr;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public Long getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Long leaveTime) {
        this.leaveTime = leaveTime;
    }

    @Override
    public String toString() {
        return "ReadingRecorder{" +
                "bookId=" + bookId +
                ", chapterId=" + chapterId +
                ", languageType=" + languageType +
                ", percent=" + percent +
                ", resourceType=" + resourceType +
                ", segmentId=" + segmentId +
                '}';
    }
}
