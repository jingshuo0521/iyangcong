package com.iyangcong.reader.bean;

/**
 * Created by sheng on 2017/3/14.
 */

public class Invite {

    private int type;
    private String targetName;
    private String userName;
    private int messageId;
    private String userId;
    private int targetId;
    private int status;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }



    public Invite(int type, String targetName, String userName, int messageId, String userId,int targetId) {
        this.type = type;
        this.targetName = targetName;
        this.userName = userName;
        this.messageId = messageId;
        this.userId = userId;
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "type=" + type +
                ", targetName='" + targetName + '\'' +
                ", userName='" + userName + '\'' +
                ", messageId=" + messageId +
                '}';
    }
}
