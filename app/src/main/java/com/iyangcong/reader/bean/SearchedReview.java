package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */

public class SearchedReview implements Serializable{
    private List<MineToughtToBook> reviewInfoList;
    private int currentPageNum;
    private int totalPageNum;
    private int totalResultNum;

    public List<MineToughtToBook> getCircleInfoList() {
        return reviewInfoList;
    }

    public void setCircleInfoList(List<MineToughtToBook> circleInfoList) {
        this.reviewInfoList = circleInfoList;
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
