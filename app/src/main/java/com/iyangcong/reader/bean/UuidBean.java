package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

public class UuidBean {
	@SerializedName("uuid")
	private String uuid;
	/**
	 * -1表示存储失败，
	 * 0表示已经有了，不需要重复写
	 * 1表示成功；
	 */
	@SerializedName("states")
	private byte state;

	public String getUuid() {
		return uuid;
	}

	public byte getState() {
		return state;
	}

	public static interface UuidState{
		byte FAILURE = -1;
		byte EXISTED = 0;
		byte SUCCESSED = 1;
	}
}
