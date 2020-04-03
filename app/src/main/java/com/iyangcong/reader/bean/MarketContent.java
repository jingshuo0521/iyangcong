package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljw on 2016/12/9.
 */

/**
 * 书城内容
 */
public class MarketContent implements Serializable {
    private List<CommonBanner> bannerList;
    private MarketRecommend bookRecommend;
    private List<CommonBroadcast> bookBroadcast;
    private List<MarketBookListItem> bookList;
    private List<MarketBookListItem> thirtpartBookList;

    public List<MarketBookListItem> getThirtpartBookList() {

//        List<MarketBookListItem> list =new ArrayList<MarketBookListItem>();
//        for(int i=0;i<3;i++){
//            MarketBookListItem item = new MarketBookListItem();
//            item.setBookAuthor("sss");
//            item.setBookName("bbb");
//            item.setBookAuthor("shao");
//            item.setBookImageUrl("https://back.iyangcong.com/onionback/upload/book/cover/mobile/cover2015060508442260897.jpg");
//            list.add(item);
//        }
//        return list;
        return thirtpartBookList;
    }

    public void setThirtpartBookList(List<MarketBookListItem> thirtpartList) {
        this.thirtpartBookList = thirtpartList;
    }

    public List<CommonBanner> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<CommonBanner> bannerList) {
        this.bannerList = bannerList;
    }

    public MarketRecommend getBookRecommend() {
        return bookRecommend;
    }

    public void setBookRecommend(MarketRecommend bookRecommend) {
        this.bookRecommend = bookRecommend;
    }

    public List<CommonBroadcast> getBookBroadcast() {
        return bookBroadcast;
    }

    public void setBookBroadcast(List<CommonBroadcast> bookBroadcast) {
        this.bookBroadcast = bookBroadcast;
    }

    public List<MarketBookListItem> getBookList() {
        return bookList;
    }

    public void setBookList(List<MarketBookListItem> bookList) {
        this.bookList = bookList;
    }
}
