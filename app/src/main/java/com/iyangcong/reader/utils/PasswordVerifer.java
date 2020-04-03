package com.iyangcong.reader.utils;

/**
 * Created by WuZepeng on 2018-04-10.
 */

public interface PasswordVerifer {
	boolean isPSWValid(String input1,String input2,int length);
	PasswordVerifer verifer = new PasswordVeriferImpl();
	class PasswordVeriferImpl implements PasswordVerifer{
		@Override
		public boolean isPSWValid(String input1, String input2, int length) {
			if(length<0)
				return input1.equals(input2);
			else
				return input1.length()>=length&&input2.length()>=length&&input1.equals(input2);
		}
	}
}
