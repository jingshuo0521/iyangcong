package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-06-09.
 */

public class PrivateMessageBean {
	/**
	 * 发送私信的用户id
	 */
	private long fromUserId;
	/**
	 * 发送私信的用户名字
	 */
	private String fromUserName;
	/**
	 * 消息id
	 */
	private int messageId;
	/**
	 * 发送时间
	 */
	private String time;
	/**
	 * 私信标题
	 */
	private String title;
	/**
	 * 发送消息总数
	 */
	private int totalNum;

	/**
	 * 私信内容
	 */
	private String body;
	/**
	 * 是否已读 1：未读 2：已读
	 */
	@SerializedName(value = "hasRead",alternate = "status")
	private int hasRead;
	public long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int isHasRead() {
		return hasRead;
	}

	public void setHasRead(int hasRead) {
		this.hasRead = hasRead;
	}
}
