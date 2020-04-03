package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class DiscoverTopicComments extends DiscoverComment {
    /**
     * 评论父话题id
     */
    private int fatherid;
    /**
     * 评论回复
     */
    private List<SubReplyComent> replays = new ArrayList<>();
//    /**
//     *用户头像
//     */
//    private String userimage;
    /**
     *总页数
     */
    @SerializedName(value = "totalpage",alternate = {"totalPage"})
    private int totalpage;
//    /**
//     * 评论时间
//     */
//    private String responsetime;
    /**
     *回复数
     */
    private int responsenum;

    public int getFatherid() {
        return fatherid;
    }

    public void setFatherid(int fatherid) {
        this.fatherid = fatherid;
    }

    public List<SubReplyComent> getReplays() {
        return replays;
    }

    public void setReplays(List<SubReplyComent> replays) {
        this.replays = replays;
    }

//    public String getUserimage() {
//        return userimage;
//    }
//
//    public void setUserimage(String userimage) {
//        super.setUserImageUrl(userimage);
//        this.userimage = userimage;
//    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

//    public String getResponsetime() {
//        return responsetime;
//    }
//
//    public void setResponsetime(String responsetime) {
//        this.responsetime = responsetime;
//    }

    public int getResponsenum() {
        return responsenum;
    }

    public void setResponsenum(int responsenum) {
        this.responsenum = responsenum;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = (o != null)&& (getResponseid() == ((DiscoverTopicComments)o).getResponseid())
                && (getCommentTime() == ((DiscoverTopicComments)o).getCommentTime());
        return result;
    }


    @Override
    public String toString() {
        return super.toString() +
                "DiscoverTopicComments{" +
                "fatherid=" + fatherid +
                ", replays=" + replays +
//                ", userimage='" + userimage + '\'' +
                ", totalpage=" + totalpage +
//                ", responsetime='" + responsetime + '\'' +
                ", responsenum=" + responsenum +
                '}';
    }
}
