package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */

public class SearchedBook implements Serializable{
    private List<MarketBookListItem> bookInfoList;
    private int currentPageNum;
    private int totalPageNum;
    private int totalResultNum;

    public List<MarketBookListItem> getBookInfoList() {
        return bookInfoList;
    }

    public void setBookInfoList(List<MarketBookListItem> bookInfoList) {
        this.bookInfoList = bookInfoList;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public int getTotalResultNum() {
        return totalResultNum;
    }

    public void setTotalResultNum(int totalResultNum) {
        this.totalResultNum = totalResultNum;
    }
}
