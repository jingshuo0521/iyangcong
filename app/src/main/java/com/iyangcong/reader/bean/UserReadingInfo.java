package com.iyangcong.reader.bean;

public class UserReadingInfo {
    private String havedReadLong;// 阅读时长
    private String overRate; // 超过百分比
    private int havedUseDays; // 使用天数
//    private int havedReadbookNums; // 阅读书数目
//    private int continueReadDaysOfFriends; //
//    private int continueReadDays;
    private String userPhoto;

    public String getHavedReadLong() {
        return havedReadLong;
    }

    public void setHavedReadLong(String havedReadLong) {
        this.havedReadLong = havedReadLong;
    }

    public String getOverRate() {
        return overRate;
    }

    public void setOverRate(String overRate) {
        this.overRate = overRate;
    }

    public int getHavedUseDays() {
        return havedUseDays;
    }

    public void setHavedUseDays(int havedUseDays) {
        this.havedUseDays = havedUseDays;
    }
//
//    public int getHavedReadbookNums() {
//        return havedReadbookNums;
//    }
//
//    public void setHavedReadbookNums(int havedReadbookNums) {
//        this.havedReadbookNums = havedReadbookNums;
//    }
//
//    public int getContinueReadDaysOfFriends() {
//        return continueReadDaysOfFriends;
//    }
//
//    public void setContinueReadDaysOfFriends(int continueReadDaysOfFriends) {
//        this.continueReadDaysOfFriends = continueReadDaysOfFriends;
//    }
//
//    public int getContinueReadDays() {
//        return continueReadDays;
//    }
//
//    public void setContinueReadDays(int continueReadDays) {
//        this.continueReadDays = continueReadDays;
//    }
//
    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
