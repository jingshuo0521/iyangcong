package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-05-24.
 * 用来接收服务器中的笔记数据
 */

public class BookNoteBean {
    /**
     * 高亮的内容相对于段首的偏移量，用来确定位置
     */
    private int offsetWord;
    /**
     * 笔记的章节id
     */
    private int chapterId;
    /**
     * 做笔记高亮的内容
     */
    private String content;
    /**
     * 笔记内容
     */
    private String note;
    /**
     * 笔记的段落id(后台数据库记录的id)
     */
    private int segmentId;
    /**
     * ios用到的偏移量
     */
    private int endOffset;
    /**
     * ios用到的偏移量
     */
    private int startOffset;
    /**
     * 后台数据库中存储的段落id减去偏移量，就是笔记真正的段落id;
     */
    private int segmentIndex;

    /**
     * 语言类型 0:双语 1：中文 2：英文
     */
    private int languagetype;
    /**
     * 笔记的评论id
     */
    @SerializedName("noteid")   // 对应 笔记的m_id
    private int commentId;
    /**
     * 学生笔记的评论id
     */
    @SerializedName("commentsId")  // 对应 笔记id（私密笔记没有此项）
    private int commentsId;
    /**
     * 书签的id;
     */
    @SerializedName("Id")
    private int bookMarkerId;

    private int iscommon; // 是否公开

    private int isdelete; // 是否删除

    public int getIscommon() {
        return iscommon;
    }

    public int getOffsetWord() {
        return offsetWord;
    }

    public void setOffsetWord(int offsetWord) {
        this.offsetWord = offsetWord;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
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

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public int getLanguagetype() {
        return languagetype;
    }

    public void setLanguagetype(int languagetype) {
        this.languagetype = languagetype;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(int commentsId) {
        this.commentsId = commentsId;
    }

    public int getBookMarkerId() {
        return bookMarkerId;
    }

    public void setBookMarkerId(int bookMarkerId) {
        this.bookMarkerId = bookMarkerId;
    }

    @Override
    public String toString() {
        return "BookNoteBean{" +
                "offsetWord=" + offsetWord +
                ", chapterId=" + chapterId +
                ", content='" + content + '\'' +
                ", note='" + note + '\'' +
                ", segmentId=" + segmentId +
                ", endOffset=" + endOffset +
                ", startOffset=" + startOffset +
                ", segmentIndex=" + segmentIndex +
                ", languagetype=" + languagetype +
                ", commentId=" + commentId +
                ", bookMarkerId=" + bookMarkerId +
                '}';
    }
}
