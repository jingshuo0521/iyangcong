package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WuZepeng on 2017-04-26.
 * 包月用的bean文件，继承了MarketBookListItem
 */

public class MonthlyMarketBookListItem extends MarketBookListItem {
    /**
     * 包月结束时间/失效时间
     */
    @SerializedName(value = "endTime", alternate = {"effectiveTime", "monthlyPaymentEndTime"})
    private String endTime;
    /**
     * 包月起始时间
     */
    @SerializedName(value = "originTime", alternate = "monthlyPaymentBeginTime")
    private String originTime;
    /**
     * 包月图书
     */
    private List<MarketBookListItem> books;
    /**
     * 购买状态 1：未购买 2：购买失效3：购买未失效
     * 在图书详情页中用到该bean文件时： 1：包月包与图书信息错误！2：未购买或包月包过期！3：有效时段！
     */
    private int status;
    /**
     * 包月包是否有效
     */
    private boolean isEffective;

    /**
     * 购买到期时间
     */
    private String deadLine;


    public List<MarketBookListItem> getBooks() {
        return books;
    }

    public void setBooks(List<MarketBookListItem> books) {
        this.books = books;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public String getOriginTime() {
        return originTime;
    }

    public void setOriginTime(String originTime) {
        this.originTime = originTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
