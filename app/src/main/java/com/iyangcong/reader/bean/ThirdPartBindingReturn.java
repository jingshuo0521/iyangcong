package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator
 */

public class ThirdPartBindingReturn implements Serializable {
    private static final long serialVersionUID = 2660692590622975958L;
    private Long userId;
    @SerializedName(value = "isSuccess",alternate = {"isbinding"})
    private int isSuccess;

    private int userType;
    private int isTeacher;
    private String semesterId;
    private String semesterName;
    private String name;

    public Long getUserId() {
        return userId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(int isTeacher) {
        this.isTeacher = isTeacher;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }
}
