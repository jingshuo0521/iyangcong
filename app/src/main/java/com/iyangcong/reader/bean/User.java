
package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户信息表
 */
@DatabaseTable(tableName = "user")
public class User implements Serializable {

    private static final long serialVersionUID = -3904553063308062712L;


    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "userId")
    private int userId;
    @DatabaseField(columnName = "userName")
    @SerializedName(value = "name")
    private String userName;
    @DatabaseField(columnName = "email")
    private String email;
    @DatabaseField(columnName = "accountName")
    private String accountName;
    @DatabaseField(columnName = "sessionId")
    private String sessionId;
    @DatabaseField(columnName = "photoUrl")
    private String photoUrl;//头像
    @DatabaseField(columnName = "gender")
    private int gender;//sex
    @DatabaseField(columnName = "semesterId")
    private int semesterId;
    @DatabaseField(columnName = "semesterName")
    private String semesterName;
    @DatabaseField(columnName = "userType")
    private int userType;

    private int isTeacher;

    private Map<String,Object> userBoundSources;

    public Map<String, Object> getUserBoundSources() {
        return userBoundSources;
    }

    public void setUserBoundSources(Map<String, Object> userBoundSources) {
        this.userBoundSources = userBoundSources;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public int getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(int isTeacher) {
        this.isTeacher = isTeacher;
    }
}
