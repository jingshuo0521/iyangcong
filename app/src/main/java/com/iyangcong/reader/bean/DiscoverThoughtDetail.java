package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/28.
 */

import org.w3c.dom.Comment;

import java.util.List;

/**
 * 读后感
 */
public class DiscoverThoughtDetail {

//    private DiscoverTopic discoverTopic;
    private DiscoverReviewDetails discoverReviewDetails;
    private List<DiscoverCircleMember> discoverCircleMemberList;

    private DiscoverComment commment;

    private List<DiscoverComment> commentList;

    public DiscoverReviewDetails getDiscoverReviewDetails() {
        return discoverReviewDetails;
    }

    public void setDiscoverReviewDetails(DiscoverReviewDetails discoverReviewDetails) {
        this.discoverReviewDetails = discoverReviewDetails;
    }

    public List<DiscoverCircleMember> getDiscoverCircleMemberList() {
        return discoverCircleMemberList;
    }

    public void setDiscoverCircleMemberList(List<DiscoverCircleMember> discoverCircleMemberList) {
        this.discoverCircleMemberList = discoverCircleMemberList;
    }

    public DiscoverComment getCommment() {
        return commment;
    }

    public void setCommment(DiscoverComment commment) {
        this.commment = commment;
    }

    public List<DiscoverComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<DiscoverComment> commentList) {
        this.commentList = commentList;
    }
}
