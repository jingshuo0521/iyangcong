package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by lg on 2016/12/25.
 */

public class BookMarketBookDetailsContent {
    private BookIntroduction bookIntroduction;
    private String authorSimpleIntroduction;
    private List<DiscoverTopic> commentList;
    private List<BaseBook> similarRecommendedList;

    public BookIntroduction getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(BookIntroduction bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public void setAuthorSimpleIntroduction(String authorSimpleIntroduction) {
        this.authorSimpleIntroduction = authorSimpleIntroduction;
    }

    public String getAuthorSimpleIntroduction() {
        return authorSimpleIntroduction;
    }

    public void setSimilarRecommendedList(List<BaseBook> similarRecommendedList) {
        this.similarRecommendedList = similarRecommendedList;
    }

    public List<BaseBook> getSimilarRecommendedList() {
        return similarRecommendedList;
    }

    public List<DiscoverTopic> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<DiscoverTopic> commentList) {
        this.commentList = commentList;
    }
}
