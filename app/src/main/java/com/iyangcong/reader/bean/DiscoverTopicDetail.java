package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class DiscoverTopicDetail {
    /**
     * 创建时间
     */
    private String createtime = "";
    /**
     *创建用户头像
     */
    private String createuserimage = "";
    /**
     * 回复人数
     */
    private int responsesum;
    /**
     *话题名称
     */
    private String topicname = "";

    private boolean haveteacher;
    private int createuser;

    private int groupId;
    /**
     * 分组名称
     */
    private String groupname;
    /**
     * 话题描述
     */
    private String topicdesc;
    /**
     * 喜欢数
     */
    private int likecount;
    /**
     * 创建名称
     */
    private String createusername;
    /**
     * 话题详情里面的html中的图片的ip；
     */
    private String path;

    private int status;

    @SerializedName("like")
    private boolean isLiked;


    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreateuserimage() {
        return createuserimage;
    }

    public void setCreateuserimage(String createuserimage) {
        this.createuserimage = createuserimage;
    }

    public int getResponsesum() {
        return responsesum;
    }

    public void setResponsesum(int responsesum) {
        this.responsesum = responsesum;
    }

    public String getTopicname() {
        return topicname;
    }

    public void setTopicname(String topicname) {
        this.topicname = topicname;
    }

    public boolean isHaveteacher() {
        return haveteacher;
    }

    public void setHaveteacher(boolean haveteacher) {
        this.haveteacher = haveteacher;
    }

    public int getCreateuser() {
        return createuser;
    }

    public void setCreateuser(int createuser) {
        this.createuser = createuser;
    }

    public int getGroupId(){return groupId; }
    public void setGroupId(int groupId){this.groupId=groupId;}

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTopicdesc() {
        return topicdesc;
    }

    public void setTopicdesc(String topicdesc) {
        this.topicdesc = topicdesc;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public String getCreateusername() {
        return createusername;
    }

    public void setCreateusername(String createusername) {
        this.createusername = createusername;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
