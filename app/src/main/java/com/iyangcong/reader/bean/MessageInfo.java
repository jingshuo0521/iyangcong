package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-06-08.
 */

public class MessageInfo{
	/**
	 * 未读信息内容
	 */
	private String action;
	/**
	 * 未读信息id
	 */
	private long messageId;
	/**
	 * 未读信息发送者
	 */
	private String messageSendUserName;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public String getMessageSendUserName() {
		return messageSendUserName;
	}

	public void setMessageSendUserName(String messageSendUserName) {
		this.messageSendUserName = messageSendUserName;
	}
}
