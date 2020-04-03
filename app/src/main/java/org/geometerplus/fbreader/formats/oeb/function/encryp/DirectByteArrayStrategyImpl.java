package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-19.
 * 针对byte[]比较短的情况，直接将byte[]转换成字符串
 */

public class DirectByteArrayStrategyImpl implements ByteArrayStrategy {
	@Override
	public String transfor(byte[] byteArr) {
		return new String(byteArr);
	}
}
