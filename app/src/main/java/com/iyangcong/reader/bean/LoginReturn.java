package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by sheng on 2017/2/14.
 */

public class LoginReturn implements Serializable {
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private String photoUrl;

    private String name;

    private int userId;
}
