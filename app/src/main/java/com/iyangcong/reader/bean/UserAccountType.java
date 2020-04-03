package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-05-31.
 * 本pojo用于装载账号的信息
 * 如果账号绑定了手机号，则mobile字段不为空,如果账号绑定了邮箱,则email字段不为空。
 */

public class UserAccountType {
	/**
	 * 用户邮箱账号
	 */
	private String email;
	/**
	 * 用户手机号
	 */
	private String mobile;
	/**
	 * 账号类型
	 */
	private int type;
	public String getEmail() {
		return email==null ? "":email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile == null?"":mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
