package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * Created by sheng on 2017/3/13.
 */

public class MessageComment implements Serializable {
    /**
     * 如果返回的消息中有关圈子，1普通圈子  2班级圈子，如果和圈子无关的话不返回改字段；
     */
    private String groupType;
    /**
     * 如果返回的消息和圈子相关，则返回圈子id;
     */
    private String groupId;
    /**
     * 该消息已读还是未读的状态  1未读  2已读
     */
    private int status;
    /**
     * 对话题评论的回复
     */
    private String childContent;
    /**
     * 行为
     */
    private String action;
    /**
     * 如果回复的内容和话题有关，则返回话题id;
     */
    private String topicId;
    /**
     * 如果回复的内容和评论有关，该字段表示话题评论；
     */
    private String parentContent;
    private String responseContent; //回复内容
    private int messageId; //回复内容Id
	//如果是书评，则该字段表示书评的对象
	private String commentedContent;

    private String responseTime; //回复时间

    private String responsedName; //被回复的内容
    private String responsedId;  //被回复的内容Id
    /**
     * 0已经删除 1回复话题 2回复读后感 3回复书摘评论
     * 4喜欢了我的评论 5喜欢了我的读后感
     * 6回复了读后感评论 7回复了话题评论
     */
    private int type;
    /**
     * 总消息数
     */
    private int totalNum;
    private String userId; //用户id
    private String userName; //用户姓名

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getChildContent() {
		return childContent;
	}

	public void setChildContent(String childContent) {
		this.childContent = childContent;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getParentContent() {
		return parentContent;
	}

	public void setParentContent(String parentContent) {
		this.parentContent = parentContent;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getResponsedName() {
		return responsedName;
	}

	public void setResponsedName(String responsedName) {
		this.responsedName = responsedName;
	}

	public String getResponsedId() {
		return responsedId;
	}

	public void setResponsedId(String responsedId) {
		this.responsedId = responsedId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
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

	public String getCommentedContent() {
		return commentedContent;
	}

	public void setCommentedContent(String commentedContent) {
		this.commentedContent = commentedContent;
	}
}
