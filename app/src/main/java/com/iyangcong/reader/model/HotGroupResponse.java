package com.iyangcong.reader.model;

import java.io.Serializable;

/**
 * Created by WuZepeng on 2017-02-22.
 * 热门圈子
 */

public class HotGroupResponse implements Serializable {
    private static final long serialVersionUID = 4862719313982370771L;
    /**圈子封面	*/
    private String cover;
    /**	圈子id*/
    private int groupid;
    /**	圈子名称*/
    private String groupname;
    /**
     * 是否是班级圈子
     */
    private boolean isClassGroup;
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public boolean isClassGroup() {
        return isClassGroup;
    }

    public void setClassGroup(boolean classGroup) {
        isClassGroup = classGroup;
    }

    @Override
    public String toString() {
        return "HotGroupResponse{" +
                "cover='" + cover + '\'' +
                ", groupid=" + groupid +
                ", groupname='" + groupname + '\'' +
                '}';
    }
}
