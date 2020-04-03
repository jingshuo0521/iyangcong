package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class SubReplyComent extends ReplyComment {
    /**
     *评论父话题id
     */
    private int fatherid;
    /**
     *评论时间
     */
//    private String responsetime = "";
    /**
     *父评论用户id
     */
    private int upuserid;
    /**
     *父评论用户名称
     */
    private String upusername = "";

    /**
     *用户头像
     */
//    private String userimage = "";

    public int getFatherid() {
        return fatherid;
    }

    public void setFatherid(int fatherid) {
        this.fatherid = fatherid;
    }

//    public String getResponsetime() {
//        return responsetime;
//    }
//
//    public void setResponsetime(String responsetime) {
//        this.responsetime = responsetime;
//    }

    public int getUpuserid() {
        return upuserid;
    }

    public void setUpuserid(int upuserid) {
        this.upuserid = upuserid;
    }

    public String getUpusername() {
        return upusername;
    }

    public void setUpusername(String upusername) {
        this.upusername = upusername;
    }

//    public String getUserimage() {
//        return userimage;
//    }
//
//    public void setUserimage(String userimage) {
//        this.userimage = userimage;
//    }
}
