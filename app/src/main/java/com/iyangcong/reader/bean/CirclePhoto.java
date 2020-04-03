package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-03-22.
 */

public class CirclePhoto {

	/**
	 * 数据库路径
	 */
	private String dbPath;
	/**
	 * 图片链接
	 */
	private String photoUrl;
	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
