package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-19.
 * byte[]数组转换成String的策略。
 * 主要是针对byte[]太长的时候避免溢出。
 */

public interface ByteArrayStrategy {

	String transfor(byte[] byteArr);
}
