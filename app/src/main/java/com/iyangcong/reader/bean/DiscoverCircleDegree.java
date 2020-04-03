package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-03-13.
 * 成员体系，用于DiscoverNewCircleInviteFriends
 */

public class DiscoverCircleDegree {
    /**
     * 成员体系id号
     */
    private int id;
    /**
     * 成员体系名称
     */
    private String title = "";
    /**
     * 是否被选中
     */
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
