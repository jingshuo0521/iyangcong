package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-03-13.
 * 用于DiscoverCircleGroup
 */

public class DiscoverCircleFriends {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 用户名字
     */
    @SerializedName(value = "userName",alternate = "name")
    private String userName = "";
    /**
     * 用户头像链接
     */
    private String photo = "";
    /**
     * 是否被选中
     */
    private boolean isChecked;
    /**
     * 用户个性签名
     */
    private String userDesc = "";
    /**
     * 是否显示
     */
    private boolean isVisibile;

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isVisibile() {
        return isVisibile;
    }

    public void setVisibile(boolean visibile) {
        isVisibile = visibile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscoverCircleFriends friends = (DiscoverCircleFriends) o;

        return userId == friends.userId;

    }

    @Override
    public int hashCode() {
        return userId;
    }

    @Override
    public String toString() {
        return "DiscoverCircleFriends{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", photo='" + photo + '\'' +
                ", isChecked=" + isChecked +
                ", userDesc='" + userDesc + '\'' +
                '}';
    }
}
