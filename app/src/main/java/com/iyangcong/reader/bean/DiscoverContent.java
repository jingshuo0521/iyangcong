package com.iyangcong.reader.bean;

import com.iyangcong.reader.model.BroadCastResponse;

import java.util.List;

/**
 * Created by ljw on 2016/12/21.
 */

/**
 * 发现页面内容
 */
public class DiscoverContent {

    private List<CommonBanner> discoverBannerList;
    private List<CommonSection> discoverSectionList;
    private List<CommonBroadcast> discoverBroadcastList;
    private List<DiscoverTopic> discoverTopicList;
    private List<DiscoverReviews> discoverReviewList;
    public List<CommonBanner> getDiscoverBannerList() {
        return discoverBannerList;
    }

    public void setDiscoverBannerList(List<CommonBanner> discoverBannerList) {
        this.discoverBannerList = discoverBannerList;
    }

    public List<CommonSection> getDiscoverSectionList() {
        return discoverSectionList;
    }

    public void setDiscoverSectionList(List<CommonSection> discoverSectionList) {
        this.discoverSectionList = discoverSectionList;
    }

    public List<CommonBroadcast> getDiscoverBroadcastList() {
        return discoverBroadcastList;
    }

    public void setDiscoverBroadcastList(List<CommonBroadcast> discoverBroadcastList) {
        this.discoverBroadcastList = discoverBroadcastList;
    }

    public List<DiscoverTopic> getDiscoverTopicList() {
        return discoverTopicList;
    }

    public List<DiscoverReviews> getDiscoverReviewList() {
        return discoverReviewList;
    }

    public void setDiscoverReviewList(List<DiscoverReviews> discoverReviewList) {
        this.discoverReviewList = discoverReviewList;
    }

    public void setDiscoverTopicList(List<DiscoverTopic> discoverTopicList) {
        this.discoverTopicList = discoverTopicList;
    }
}
