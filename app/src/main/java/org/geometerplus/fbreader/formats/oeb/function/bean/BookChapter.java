package org.geometerplus.fbreader.formats.oeb.function.bean;

import java.util.List;

public class BookChapter {

    public int chapterId;
    public List<BookSegment> segmentList;
    public String chapterName;
    public String chapterSrc;
    public String chapterNo;
    
    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public List<BookSegment> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(List<BookSegment> segmentList) {
        this.segmentList = segmentList;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterSrc() {
        return chapterSrc;
    }

    public void setChapterSrc(String chapterSrc) {
        this.chapterSrc = chapterSrc;
    }
    public String getChapterNo() {
        return chapterNo;
    }
    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }
    
}
