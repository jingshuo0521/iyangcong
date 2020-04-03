package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/20.
 */

public class MarketRecommend implements Serializable {
    /*
    分类图片
     */
    private String categoryP = new String();
    /*
    优惠图片
     */
    private String  cutP = new String();
    /*
    免费图片
     */
    private String   freeP = new String();
    /*
    包月图片
     */
    private String   monthP = new String();
    /*
    新书图片
     */
    private String   newbookP = new String();
    /*
    限时免费
    */
    private MarketToddayPush free;
    /*
    热门推荐
    */
    private MarketToddayPush hot;
    /*
    通识阅读
    */
    private MarketToddayPush tongshi;

    public String getCategoryP() {
        return categoryP;
    }

    public void setCategoryP(String categoryP) {
        this.categoryP = categoryP;
    }

    public String getCutP() {
        return cutP;
    }

    public void setCutP(String cutP) {
        this.cutP = cutP;
    }

    public String getFreeP() {
        return freeP;
    }

    public void setFreeP(String freeP) {
        this.freeP = freeP;
    }

    public String getMonthP() {
        return monthP;
    }

    public void setMonthP(String monthP) {
        this.monthP = monthP;
    }

    public String getNewbookP() {
        return newbookP;
    }

    public void setNewbookP(String newbookP) {
        this.newbookP = newbookP;
    }

    public MarketToddayPush getFree() {
        return free;
    }

    public void setFree(MarketToddayPush free) {
        this.free = free;
    }

    public MarketToddayPush getHot() {
        return hot;
    }

    public void setHot(MarketToddayPush hot) {
        this.hot = hot;
    }

    public MarketToddayPush getTongshi() {
        return tongshi;
    }

    public void setTongshi(MarketToddayPush tongshi) {
        this.tongshi = tongshi;
    }
}
