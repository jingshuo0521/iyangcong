package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */

public class DiscoverCooperationUnit {
    private String UnitImg;
    private List<DiscoverReadPartyExercise> discoverCooperationUnitActivityList;
    private List<CommonVideo> commonVideoList;

    public String getUnitImg() {
        return UnitImg;
    }

    public List<DiscoverReadPartyExercise> getDiscoverCooperationUnitActivityList() {
        return discoverCooperationUnitActivityList;
    }

    public void setUnitImg(String unitImg) {
        UnitImg = unitImg;
    }

    public void setDiscoverCooperationUnitActivityList(List<DiscoverReadPartyExercise> discoverCooperationUnitActivityList) {
        this.discoverCooperationUnitActivityList = discoverCooperationUnitActivityList;
    }

    public List<CommonVideo> getCommonVideoList() {
        return commonVideoList;
    }

    public void setCommonVideoList(List<CommonVideo> commonVideoList) {
        this.commonVideoList = commonVideoList;
    }
}
