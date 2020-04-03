package com.iyangcong.reader.bean;

import org.geometerplus.fbreader.book.Bookmark;

/**
 * Created by DarkFlameMaster on 2017/5/17.
 */

public class AndroidBookMark {
    private int id;
    private String uid;
    private String bookUid;
    private String bookTitle;
    private String myText;
    private long creationTimestamp;
    private int isVisible;
    private int styleId;
    private int start_paragraphIndex;
    private int end_paragraphIndex;
    private int start_wordIndex;
    private int start_charIndex;
    private int end_wordIndex;
    private int end_charIndex;
    private String myOriginalText;
    private String myVersionUid;
    private int isdelete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookUid() {
        return bookUid;
    }

    public void setBookUid(String bookUid) {
        this.bookUid = bookUid;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getEnd_charIndex() {
        return end_charIndex;
    }

    public void setEnd_charIndex(int end_charIndex) {
        this.end_charIndex = end_charIndex;
    }

    public int getEnd_paragraphIndex() {
        return end_paragraphIndex;
    }

    public void setEnd_paragraphIndex(int end_paragraphIndex) {
        this.end_paragraphIndex = end_paragraphIndex;
    }

    public int getEnd_wordIndex() {
        return end_wordIndex;
    }

    public void setEnd_wordIndex(int end_wordIndex) {
        this.end_wordIndex = end_wordIndex;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        this.isVisible = isVisible;
    }

    public String getMyOriginalText() {
        return myOriginalText;
    }

    public void setMyOriginalText(String myOriginalText) {
        this.myOriginalText = myOriginalText;
    }

    public String getMyText() {
        return myText;
    }

    public void setMyText(String myText) {
        this.myText = myText;
    }

    public String getMyVersionUid() {
        return myVersionUid;
    }

    public void setMyVersionUid(String myVersionUid) {
        this.myVersionUid = myVersionUid;
    }

    public int getStart_charIndex() {
        return start_charIndex;
    }

    public void setStart_charIndex(int start_charIndex) {
        this.start_charIndex = start_charIndex;
    }

    public int getStart_paragraphIndex() {
        return start_paragraphIndex;
    }

    public void setStart_paragraphIndex(int start_paragraphIndex) {
        this.start_paragraphIndex = start_paragraphIndex;
    }

    public int getStart_wordIndex() {
        return start_wordIndex;
    }

    public void setStart_wordIndex(int start_wordIndex) {
        this.start_wordIndex = start_wordIndex;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
