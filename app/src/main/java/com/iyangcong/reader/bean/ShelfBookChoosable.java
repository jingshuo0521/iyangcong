package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-06-15.
 */

public class ShelfBookChoosable extends ShelfBook {
	private boolean isChoosed;

	public boolean isChoosed() {
		return isChoosed;
	}

	public void setChoosed(boolean choosed) {
		isChoosed = choosed;
	}
}
