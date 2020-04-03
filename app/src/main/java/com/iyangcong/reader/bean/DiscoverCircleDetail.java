package com.iyangcong.reader.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljw on 2016/12/26.
 */

/**
 * 圈子详情页
 */
public class DiscoverCircleDetail {
    /**
     * 圈子详情
     */
    private DiscoverCircleDescribe discoverCircleDescribe = new DiscoverCircleDescribe();
    /**
     * 圈子标签列表
     */
    private List<String> circleLabelList = new ArrayList<String>();;
    /**
     * 圈子成员列表
     */
    private List<DiscoverCircleMember> circleMemberList = new ArrayList<DiscoverCircleMember>();
    /**
     * 圈子图书列表
     */
    private List<ShelfBook> shelfBookList = new ArrayList<ShelfBook>();;
    /**
     * 话题列表
     */
    private List<DiscoverTopic> discoverTopicList = new ArrayList<DiscoverTopic>();

    public DiscoverCircleDescribe getDiscoverCircleDescribe() {
        return discoverCircleDescribe;
    }

    public void setDiscoverCircleDescribe(DiscoverCircleDescribe discoverCircleDescribe) {
        this.discoverCircleDescribe = discoverCircleDescribe;
    }

    public List<String> getCircleLabelList() {
        return circleLabelList;
    }

    public void setCircleLabelList(List<String> circleLabelList) {
        this.circleLabelList = circleLabelList;
    }

    public List<DiscoverCircleMember> getCircleMemberList() {
        return circleMemberList;
    }

    public void setCircleMemberList(List<DiscoverCircleMember> circleMemberList) {
        this.circleMemberList = circleMemberList;
    }

    public List<ShelfBook> getShelfBookList() {
        return shelfBookList;
    }

    public void setShelfBookList(List<ShelfBook> shelfBookList) {
        this.shelfBookList = shelfBookList;
    }

    public List<DiscoverTopic> getDiscoverTopicList() {
        return discoverTopicList;
    }

    public void setDiscoverTopicList(List<DiscoverTopic> discoverTopicList) {
        this.discoverTopicList = discoverTopicList;
    }
}
