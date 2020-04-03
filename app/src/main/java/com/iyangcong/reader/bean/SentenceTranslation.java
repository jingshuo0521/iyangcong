package com.iyangcong.reader.bean;

/**
 * Created by DarkFlameMaster on 2017/5/7.
 */

public class SentenceTranslation {
    private int chapterid;
    private int id;
    private String segment_en;
    private String segment_zh;

    public int getChapterid() {
        return chapterid;
    }

    public void setChapterid(int chapterid) {
        this.chapterid = chapterid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSegment_en() {
        return segment_en;
    }

    public void setSegment_en(String segment_en) {
        this.segment_en = segment_en;
    }

    public String getSegment_zh() {
        return segment_zh;
    }

    public void setSegment_zh(String segment_zh) {
        this.segment_zh = segment_zh;
    }
}
