package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by sheng on 2017/3/14.
 */

public class MessageNoticeInvite implements Serializable {

    private int type; //此条消息的类型

    private int targetId; //圈子id
    private String targetName; //圈子名称

    private int messageId; //此条消息id
    private String time; //此条消息时间

    private String userId; //用户id
    private String userName; //用户名字
    //总的消息数
    private int totalNum;
    //消息已读状态 1：未读 2：已读
    private int status;
    private String action;
    //如果是邀请读书，则该字段有值
    private String bookId;
    //如果是邀请看书，则该字段有值
    private String bookName;
    //如果是与圈子有关，该字段有值
    private String groupId;
    //如果是与圈子有关，该字段有值
    private String groupName;
    //资源被删除时，该字段有值
    private String responseContent;
    //资源被删除时，该字段有值
    private String responsedId;
    //资源被删除时，该字段有值
    private String responsedName;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getResponsedId() {
        return responsedId;
    }

    public void setResponsedId(String responsedId) {
        this.responsedId = responsedId;
    }

    public String getResponsedName() {
        return responsedName;
    }

    public void setResponsedName(String responsedName) {
        this.responsedName = responsedName;
    }
}
