package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-19.
 * AES解密流程。
 * 因为解密的时候，一次性只能读入固定字节数的字符数组。
 * 会产生有一部分字符是上一次解密需要的字符，
 * 有一部分字符需要拼接上上一次解密没有用完的字符串才能正常解密。
 */

public interface AESProcessor {
	/**
	 * 获取上次没有使用完的密文，如果有的话拼接到本次要解密的密文中，再进行解密
	 * @param decryptMessage
	 * @return
	 */
	String[] getPreixMessage(String decryptMessage);
//	StringBuilder getPreixMessage(StringBuilder stringBuilder, String decryptMessage);

	/**
	 * 保存本次解密以后剩下的字符串，放到下一次进行解密。
	 * @param decrpMessage
	 */
	void saveSuffixMessage(String decrpMessage);

	/**
	 * 解密正常能够解密的密文
	 * @param decryptMessage
	 * @return
	 */
	String handleNormalMessage(String[] decryptMessage);
//	StringBuilder handleNormalMessage(StringBuilder stringBuilder, String decryptMessage);
}
