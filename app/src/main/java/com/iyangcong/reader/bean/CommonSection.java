package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/9.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 通用版块
 */
public class CommonSection implements Serializable {
    /**
     * 板块id
     */
    private int sectionId;
    /**
     * 版块名称
     */
    private String sectionName = "";
    /**
     * 图片url
     */
    @SerializedName(value = "image")
    private String sectionImageUrl = "";
    /**
     * 版块类型
     */
    private int sectionType;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionImageUrl() {
        return sectionImageUrl;
    }

    public void setSectionImageUrl(String sectionImageUrl) {
        this.sectionImageUrl = sectionImageUrl;
    }

    public int getSectionType() {
        return sectionType;
    }

    public void setSectionType(int sectionType) {
        this.sectionType = sectionType;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
}
