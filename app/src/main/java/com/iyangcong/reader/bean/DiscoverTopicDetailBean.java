package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class DiscoverTopicDetailBean {
    private DiscoverTopicDetail discoverTopicDetail;
    private List<DiscoverCircleMember> discoverCircleMemberList;
    private List<DiscoverTopicComments> discoverTopicCommentList;

    public List<DiscoverTopicComments> getDiscoverTopicCommentList() {
        return discoverTopicCommentList;
    }

    public void setDiscoverTopicCommentList(List<DiscoverTopicComments> discoverTopicCommentList) {
        this.discoverTopicCommentList = discoverTopicCommentList;
    }

    public List<DiscoverCircleMember> getDiscoverCircleMemberList() {
        return discoverCircleMemberList;
    }

    public void setDiscoverCircleMemberList(List<DiscoverCircleMember> discoverCircleMemberList) {
        this.discoverCircleMemberList = discoverCircleMemberList;
    }

    public DiscoverTopicDetail getDiscoverTopicDetail() {
        return discoverTopicDetail;
    }

    public void setDiscoverTopicDetail(DiscoverTopicDetail discoverTopicDetail) {
        this.discoverTopicDetail = discoverTopicDetail;
    }
}
