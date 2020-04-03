package com.iyangcong.reader.event;

/**
 * Created by WuZepeng on 2018-04-09.
 */

public class SlideEvent {
	int index = 0;

	public SlideEvent(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
