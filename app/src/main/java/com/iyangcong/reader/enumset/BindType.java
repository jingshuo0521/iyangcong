package com.iyangcong.reader.enumset;

/**
 * Created by WuZepeng on 2017-06-19.
 */

public enum BindType {
	TELEPHONE(true),
	EMAIL(false);
	boolean type;
	BindType(boolean type){
		this.type = type;
	}
}
