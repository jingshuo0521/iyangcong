package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ljw on 2017/1/4.
 */

public class CommonVideo {
    /**
     * 读书会名字
     */
    @SerializedName("clubname")
    private String clubName;

    /**
     * 读书会简介
     */
    private String guide;
    /**
     * 视频id
     */
    private int id;
    /**
     *视频缩略图
     */
    @SerializedName("image")
    private String videoImage = "";
    /**
     * 视频路径
     */
    @SerializedName("resource")
    private String videoUrl = "";

    private String vedioTitle = "";


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public String getVedioTitle() {
        return vedioTitle;
    }

    public void setVedioTitle(String vedioTitle) {
        this.vedioTitle = vedioTitle;
    }
}
