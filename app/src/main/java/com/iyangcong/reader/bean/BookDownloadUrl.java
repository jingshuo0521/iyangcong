package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ljw on 2017/3/15.
 */

public class BookDownloadUrl {
    @SerializedName(value = "imgurl")
    private String imgUrl;
    @SerializedName(value = "resourceurl")
    private String resourceUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }
}
