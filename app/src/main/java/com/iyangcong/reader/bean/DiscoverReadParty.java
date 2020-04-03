package com.iyangcong.reader.bean;

import java.util.List;


/**
 * @author ljw
 *         读书会首页
 */
public class DiscoverReadParty {
    private List<CommonBanner> commonBannerList;
    private List<DiscoverReadPartyExercise> discoverReadPartyActivityList;
    private List<CommonVideo> commonVedioList;
    private List<Orgernization> cooperationUnitList;

    public List<CommonBanner> getCommonBannerList() {
        return commonBannerList;
    }

    public void setCommonBannerList(List<CommonBanner> commonBannerList) {
        this.commonBannerList = commonBannerList;
    }

    public List<DiscoverReadPartyExercise> getDiscoverReadPartyActivityList() {
        return discoverReadPartyActivityList;
    }

    public void setDiscoverReadPartyActivityList(List<DiscoverReadPartyExercise> discoverReadPartyActivityList) {
        this.discoverReadPartyActivityList = discoverReadPartyActivityList;
    }

    public List<CommonVideo> getCommonVedioList() {
        return commonVedioList;
    }

    public void setCommonVedioList(List<CommonVideo> commonVedioList) {
        this.commonVedioList = commonVedioList;
    }

    public List<Orgernization> getCooperationUnitList() {
        return cooperationUnitList;
    }

    public void setCooperationUnitList(List<Orgernization> cooperationUnitList) {
        this.cooperationUnitList = cooperationUnitList;
    }
}
