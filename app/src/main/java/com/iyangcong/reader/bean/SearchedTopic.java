package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljw on 2017/3/20.
 */

public class SearchedTopic implements Serializable{
    private List<DiscoverTopic> topicInfoList;
    private int currentPageNum;
    private int totalPageNum;
    private int totalResultNum;

    public List<DiscoverTopic> getCircleInfoList() {
        return topicInfoList;
    }

    public void setCircleInfoList(List<DiscoverTopic> circleInfoList) {
        this.topicInfoList = circleInfoList;
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
