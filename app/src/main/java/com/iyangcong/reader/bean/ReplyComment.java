package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-03-16.
 * used in DiscoverComment
 */

public class ReplyComment {
    /**
     * 评论内容
     */
    private String content = "";
    /**
     * 喜欢数
     */
    private int likecount;
    /**
     *回复时间
     */
    @SerializedName(value = "responsedate",alternate = {"responsetime"})
    private String responsedate;
    /**
     *回复id
     */
    private int responseid;
    /**
     *用户id
     */
    private int userid;
    /**
     *用户名称
     */
    private String username;
    /**
     *用户头像
     */
    @SerializedName(value = "userphoto",alternate = {"userimage"})
    private String userphoto;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public String getResponsedate() {
        return responsedate;
    }

    public void setResponsedate(String responsedate) {
        this.responsedate = responsedate;
    }

    public int getResponseid() {
        return responseid;
    }

    public void setResponseid(int responseid) {
        this.responseid = responseid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    @Override
    public String toString() {
        return "ReplyComment{" +
                "content='" + content + '\'' +
                ", likecount=" + likecount +
                ", responsedate='" + responsedate + '\'' +
                ", responseid=" + responseid +
                ", userid=" + userid +
                ", username='" + username + '\'' +
                ", userphoto='" + userphoto + '\'' +
                '}';
    }
}
