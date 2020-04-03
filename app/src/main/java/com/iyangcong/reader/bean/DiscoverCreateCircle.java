package com.iyangcong.reader.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by WuZepeng on 2017-03-14.
 * 用于创建圈子
 */

public class DiscoverCreateCircle implements Parcelable {
    /**
     * 权限等级
     */
    private int authority;
    /**
     * 圈子分类
     */
    private int category;
    /**
     * 圈子头像
     */
    private String cover = "";
    /**
     *成长体系
     */
    private int degree;
    /**
     *圈子描述
     */
    private String groupdesc = "";
    /**
     *圈子名称
     */
    private String groupname = "";
    /**
     *添加的图书id字符串
     */
    private String sbooks = "";
    /**
     *邀请好友用户id字符串
     */
    private String sfriends = "";
    /**
     *标签
     */
    private String tag = "";
    /**
     * 图片路径的相对路径
     */
    private String path;
    public static final Parcelable.Creator<DiscoverCreateCircle> CREATOR = new Creator<DiscoverCreateCircle>() {
        @Override
        public DiscoverCreateCircle createFromParcel(Parcel parcel) {
            DiscoverCreateCircle circle = new DiscoverCreateCircle();
            circle.authority = parcel.readInt();
            circle.category = parcel.readInt();
            circle.cover = parcel.readString();
            circle.degree = parcel.readInt();
            circle.groupdesc = parcel.readString();
            circle.groupname = parcel.readString();
            circle.sbooks = parcel.readString();
            circle.sfriends = parcel.readString();
            circle.tag = parcel.readString();
            circle.path = parcel.readString();
            return circle;
        }

        @Override
        public DiscoverCreateCircle[] newArray(int i) {
            return new DiscoverCreateCircle[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(authority);
        parcel.writeInt(category);
        parcel.writeString(cover);
        parcel.writeInt(degree);
        parcel.writeString(groupdesc);
        parcel.writeString(groupname);
        parcel.writeString(sbooks);
        parcel.writeString(sfriends);
        parcel.writeString(tag);
        parcel.writeString(path);
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getGroupdesc() {
        return groupdesc;
    }

    public void setGroupdesc(String groupdesc) {
        this.groupdesc = groupdesc;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getSbooks() {
        return sbooks;
    }

    public void setSbooks(String sbooks) {
        this.sbooks = sbooks;
    }

    public String getSfriends() {
        return sfriends;
    }

    public void setSfriends(String sfriends) {
        this.sfriends = sfriends;
    }

    public String getTag() {
        return tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTag(List<CircleLabel> tmp){
        this.tag = formatTags(tmp);
    }

    public String formatTags(List<CircleLabel> tmp){
        StringBuilder sb = new StringBuilder("");
        if(tmp.size() == 0){

        }else if(tmp.size() == 1){
            sb.append(tmp.get(0).getTagName());
        }else if(tmp.size() > 1){
            sb.append(tmp.get(0).getTagName());
            for(int index = 1; index < tmp.size();index++){
                sb.append(","+tmp.get(index).getTagName());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "DiscoverCreateCircle{" +
                "authority=" + authority +
                ", category=" + category +
                ", cover='" + cover + '\'' +
                ", degree=" + degree +
                ", groupdesc='" + groupdesc + '\'' +
                ", groupname='" + groupname + '\'' +
                ", sbooks='" + sbooks + '\'' +
                ", sfriends='" + sfriends + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
