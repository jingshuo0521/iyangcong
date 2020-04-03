package com.iyangcong.reader.bean;

public class FansBean extends DiscoverCircleFriends {
	public static final int FRIEND = 1;
	public static final int NOT_FIREND = 0;
	/**
	 * 是否关注 0:没有专注，1：已经关注；
	 */
	int isFriend;

	public boolean isFriend() {
		return isFriend==FRIEND;
	}

	public void setFriend(int friend) {
		isFriend = friend;
	}
}
