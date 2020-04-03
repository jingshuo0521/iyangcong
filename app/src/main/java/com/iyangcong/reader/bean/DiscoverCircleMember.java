package com.iyangcong.reader.bean;

/**
 * Created by ljw on 2016/12/26.
 */

public class DiscoverCircleMember extends CommonSection{
    /**
     *成员活跃度
     */
    private int activilevel;
    /**
     * 成员id
     */
    private int circleMemberId;
    /**
     * 头像
     */
    private String circleMemberImgUrl = "";
    /**
     * 名称
     */
    private String circleMemberName = "";
    /**
     * 是否加入黑名单
     */
    private boolean isforbidden;

    public int getActivilevel() {
        return activilevel;
    }

    public void setActivilevel(int activilevel) {
        this.activilevel = activilevel;
    }

    public int getCircleMemberId() {
        return circleMemberId;
    }

    public void setCircleMemberId(int circleMemberId) {
        this.circleMemberId = circleMemberId;

    }

    public String getCircleMemberImgUrl() {
        return circleMemberImgUrl;
    }

    public void setCircleMemberImgUrl(String circleMemberImgUrl) {
        this.circleMemberImgUrl = circleMemberImgUrl;
        setSectionImageUrl(circleMemberImgUrl);
    }

    public String getCircleMemberName() {
        return circleMemberName;
    }

    public void setCircleMemberName(String circleMemberName) {
        this.circleMemberName = circleMemberName;
        setSectionName(circleMemberName);
    }

    public boolean isforbidden() {
        return isforbidden;
    }

    public void setIsforbidden(boolean isforbidden) {
        this.isforbidden = isforbidden;
    }

    @Override
    public boolean equals(Object o) {
        return o== null &&((DiscoverCircleMember)o).getCircleMemberId()==circleMemberId;
    }

    @Override
    public int hashCode() {
        int result = activilevel;
        result = 31 * result + circleMemberId;
        result = 31 * result + (circleMemberImgUrl != null ? circleMemberImgUrl.hashCode() : 0);
        result = 31 * result + (circleMemberName != null ? circleMemberName.hashCode() : 0);
        result = 31 * result + (isforbidden ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DiscoverCircleMember{" +
                "activilevel=" + activilevel +
                ", circleMemberId=" + circleMemberId +
                ", circleMemberImgUrl='" + circleMemberImgUrl + '\'' +
                ", circleMemberName='" + circleMemberName + '\'' +
                ", isforbidden=" + isforbidden +
                '}';
    }
}
