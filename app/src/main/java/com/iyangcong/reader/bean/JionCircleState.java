package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-04-13.
 */

public class JionCircleState {
	/**
	 * 创建圈子的用户的id
	 */
	private int createuser;
	/**
	 * 本app用户是否加入圈子的信息；
	 */
	private boolean isjoined;

	/**
	 *圈子类型
	 */
	private int grouptype;

	/**
	 * 圈子权限
	 */
	private int authority;

	/**
	 *图片路径的前缀
	 */
	private String path;
	/**
	 * 审核状态
	 * 1:未审核 2 审核通过  3审核未通过 4：删除5.举报
	 */
	private int checkstatus;

	public int getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(int checkstatus) {
		this.checkstatus = checkstatus;
	}

	public boolean isjoined() {
		return isjoined;
	}

	public void setIsjoined(boolean isjoined) {
		this.isjoined = isjoined;
	}

	public int getCreateuser() {
		return createuser;
	}

	public void setCreateuser(int createuser) {
		this.createuser = createuser;
	}

	public int getGrouptype() {
		return grouptype;
	}

	public void setGrouptype(int grouptype) {
		this.grouptype = grouptype;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
