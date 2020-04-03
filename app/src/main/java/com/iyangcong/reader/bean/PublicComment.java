package com.iyangcong.reader.bean;

public class PublicComment {
    private String content;
    private String last_modified;
    private int id;
    private int bookid;
    private int allcount;
    private int isdelete;
    private int type;
    private int chapterid;
    private int languagetype;
    private int segmentid;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChapterid() {
        return chapterid;
    }

    public void setChapterid(int chapterid) {
        this.chapterid = chapterid;
    }

    public int getLanguagetype() {
        return languagetype;
    }

    public void setLanguagetype(int languagetype) {
        this.languagetype = languagetype;
    }

    public int getSegmentid() {
        return segmentid;
    }

    public void setSegmentid(int segmentid) {
        this.segmentid = segmentid;
    }
}
