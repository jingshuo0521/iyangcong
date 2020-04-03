package com.iyangcong.reader.bean;

/**
 * @author ljw
 * 2016/12/9.
 */

import java.io.Serializable;

/**
 * 通用轮播图
 */
public class CommonBanner implements Serializable {

    private static final long serialVersionUID = 7416534029544850402L;

    /**
     * 轮播图id
     */
    private int id;

    /**
     * 轮播图标题
     */
    private String title = "";
    /**
     * 轮播图内容
     */
    private String content = "";
    /**
     * 轮播图跳转链接
     */
    private String htmlUrl = "";
    /**
     * 轮播图url
     */
    private String bannerUrl = "";
    /**
     * 轮播图类型，用于指定跳转页面
     */
    private int bannerType;

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public int getBannerType() {
        return bannerType;
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

}
