package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2017/1/6.
 */

public class DiscoverReadPartyExercise {
    /**
     * id
     */
    private int clubId;
    /**
     * 名称
     */
    private String clubName;
    /**
     * 是否临近
     */
    private boolean isComing;
    /**
     * 地点
     */
    private String location;
    /**
     * 时间
     */
    private String time;

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isComing() {
        return isComing;
    }

    public void setComing(boolean coming) {
        isComing = coming;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
