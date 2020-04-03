package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljw on 2016/12/28.
 */

public class DiscoverComment {
    /**
     * 用户头像
     */
    @SerializedName(value = "userphoto",alternate = {"userimage","userPhoto"})
    private String userImageUrl = "";
    /**
     * 用户名
     */
    @SerializedName(value = "username",alternate = {"userName"})
    private String userName = "";
    /**
     * 用户id
     */
    @SerializedName(value = "userid",alternate = {"userId"})
    private int userid;
    /**
     * 回复id
     */
    @SerializedName(value = "responseid",alternate = {"id"})
    private int responseid;
    /**
     * 评论发布时间
     */
    @SerializedName(value = "responsedate",alternate = {"responsetime","responseDate"})
    private String commentTime = "";
    /**
     * 评论内容
     */
    @SerializedName("content")
    private String commentContent = "";
    /**
     * 喜欢数
     */
    @SerializedName(value = "likecount",alternate = {"likeCount"})
    private int likecount;
    /**
     *评论回复
     */
    @SerializedName(value = "replys")
    private List<ReplyComment> replys;

    /**
     * 回复内容的html中的相对路径的前缀
     */
    private String path;

    /**
     * 是否被赞过
     */
    @SerializedName("like")
    private boolean isLike;

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getResponseid() {
        return responseid;
    }

    public void setResponseid(int responseid) {
        this.responseid = responseid;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public List<ReplyComment> getReplys() {
        return replys;
    }

    public void setReplys(List<ReplyComment> replys) {
        this.replys = replys;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean isLike) {
        this.isLike = isLike;
    }

    @Override
    public String toString() {
        return "DiscoverComment{" +
                "userImageUrl='" + userImageUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", userid=" + userid +
                ", responseid=" + responseid +
                ", commentTime='" + commentTime + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", likecount=" + likecount +
                ", replys=" + replys +
                '}';
    }
}
