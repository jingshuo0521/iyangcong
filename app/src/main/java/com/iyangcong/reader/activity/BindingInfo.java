package com.iyangcong.reader.activity;

/**
 * Created by WuZepeng on 2017-05-31.
 */

class BindingInfo {
	/**
	 * 0已绑定到账户 1.账户未注册 2账户已绑定了相同类型的第三方账号
	 */
	private int isSuccess;
	/**
	 *用户id
	 */
	private long userId;

	public int getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
