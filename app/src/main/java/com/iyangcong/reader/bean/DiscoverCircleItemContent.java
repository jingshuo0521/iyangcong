package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lg on 2016/12/27.
 */

public class DiscoverCircleItemContent {
    @SerializedName(value = "circleImgUrl",alternate ={"groupCover"})
    String imgUrl;
    @SerializedName(value = "circleName",alternate ={"groupName"})
    String name;
    @SerializedName(value = "circleId",alternate ={"groupId"})
    private int circleId;

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DiscoverCircleItemContent{" +
                "imgUrl='" + imgUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
