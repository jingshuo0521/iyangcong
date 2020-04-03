package com.iyangcong.reader.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 */

public class DiscoverReadingPartyDetailsContent {
    /**
     * 导读
     */
    private String guide;
    /**
     * 读书会背景图
     */
    private String image;
    /**
     * 活动地址
     */
    private String location;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 主办机构
     */
    private String organization;
    /**
     * 区域
     */
    private String regin;
    /**
     * 主题
     */
    private String subject;

    /**
     * 时期
     */
    private String period;
    /**
     * 举办时间
     */
    private String time;
    /**
     * 报名链接二维码链接
     */
    private String url;

    private String adviser;

    private List<CommonVideo> reviewVideoList = new ArrayList<>();


    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getAdviser() {
        return adviser;
    }

    public void setAdviser(String adviser) {
        this.adviser = adviser;
    }

    public String getRegin() {
        return regin;
    }

    public void setRegin(String regin) {
        this.regin = regin;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<CommonVideo> getReviewVideoList() {
        return reviewVideoList;
    }

    public void setReviewVideoList(List<CommonVideo> reviewVideoList) {
        this.reviewVideoList = reviewVideoList;
    }
}
