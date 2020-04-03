package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Changed by DrakFlameMaster on 2017/5/12.
 */
/*
个人主页圈子
 */
public class MineCircle implements Serializable {
    private static final long serialVersionUID = 4862719313982370771L;
    /*
   标签
    */
    private ArrayList<String> tags;
    /*
    圈子id
     */
    @SerializedName(value = "groupId",alternate = "id")
    private int groupId;
    /*
    话题总数
     */

    private int totalNum;

    private int authority;
    /*
    圈子封面
     */
    private String cover;
    /*
    是否班级圈子
     */
    private boolean isclassgroup;
    /*
    是否可编辑
     */
    private boolean iseditable;
    /*
    话题总数
    */
    @SerializedName(value = "topicsum" , alternate = "topicSum")
    private int topicsum;
    /*
    成员数
    */
    @SerializedName(value = "membersum" , alternate = "memberSum")
    private int membersum;
    /*
    圈子名称
    */
    @SerializedName(value = "groupname" , alternate = "groupName")
    private String groupname;
    /*
    话题页数
     */
    private int totalpage;
    /*

     */
    private int checkstatus;

    public int getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(int checkstatus) {
        this.checkstatus = checkstatus;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public boolean isclassgroup() {
        return isclassgroup;
    }

    public void setIsclassgroup(boolean isclassgroup) {
        this.isclassgroup = isclassgroup;
    }

    public boolean iseditable() {
        return iseditable;
    }

    public void setIseditable(boolean iseditable) {
        this.iseditable = iseditable;
    }

    public int getMembersum() {
        return membersum;
    }

    public void setMembersum(int membersum) {
        this.membersum = membersum;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getTopicsum() {
        return topicsum;
    }

    public void setTopicsum(int topicsum) {
        this.topicsum = topicsum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }
}
