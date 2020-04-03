package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by WuZepeng on 2017-06-08.
 */

public class UnreadMsgBean {
	/**
	 * 未读消息内容
	 */
	private List<MessageInfo> messageInfo;
	/**
	 * 未读消息数
	 */
	private int noReadMessageNum;

	public List<MessageInfo> getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(List<MessageInfo> messageInfo) {
		this.messageInfo = messageInfo;
	}

	public int getNoReadMessageNum() {
		return noReadMessageNum;
	}

	public void setNoReadMessageNum(int noReadMessageNum) {
		this.noReadMessageNum = noReadMessageNum;
	}

}
