package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/9.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 通用广播
 */
public class CommonBroadcast implements Serializable {

    /**
     * 广播id
     */
    private int id;
    /**
     * 广播url
     */
    @SerializedName(value = "broadcastUrl",alternate = {"imgUrl"})
    private String broadcastUrl = "";
    /**
     * 广播内容
     */
    private String broadcastContent = "";
    /**
     * 广播标题
     */
    private String broadcastTitle = "";


    private String content = "";
    /**
     * 广播类型，用于指定跳转页面
     * 0：单册图书推荐，1:图书专题，2:圈子推荐，3:读书会推荐，4:图书分类，5:其他
     */
    private int broadcastType;



    public String getBroadcastUrl() {
        return broadcastUrl;
    }

    public void setBroadcastUrl(String broadcastUrl) {
        this.broadcastUrl = broadcastUrl;
    }

    public String getBroadcastContent() {
        return broadcastContent;
    }

    public void setBroadcastContent(String broadcastContent) {
        this.broadcastContent = broadcastContent;
    }

    public int getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(int broadcastType) {
        this.broadcastType = broadcastType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBroadcastTitle() {
        return broadcastTitle;
    }

    public void setBroadcastTitle(String broadcastTitle) {
        this.broadcastTitle = broadcastTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommonBroadcast{" +
                "id=" + id +
                ", broadcastUrl='" + broadcastUrl + '\'' +
                ", broadcastContent='" + broadcastContent + '\'' +
                ", broadcastTitle='" + broadcastTitle + '\'' +
                ", broadcastType=" + broadcastType +
                '}';
    }
}
