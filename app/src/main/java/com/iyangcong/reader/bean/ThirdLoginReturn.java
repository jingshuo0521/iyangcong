package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class ThirdLoginReturn implements Serializable{
    private Long userId;
    private int isbinding;
    private int semesterId;
    private String semesterName;
    private int userType;


    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getIsbinding() {
        return isbinding;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIsbinding(int isbinding) {
        this.isbinding = isbinding;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
