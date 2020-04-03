package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */

public class SearchedCircle implements Serializable{
    private List<DiscoverCircleItemContent> groupsInfoList;
    private int currentPageNum;
    private int totalPageNum;
    private int totalResultNum;

    public List<DiscoverCircleItemContent> getCircleInfoList() {
        return groupsInfoList;
    }

    public void setCircleInfoList(List<DiscoverCircleItemContent> circleInfoList) {
        this.groupsInfoList = circleInfoList;
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
