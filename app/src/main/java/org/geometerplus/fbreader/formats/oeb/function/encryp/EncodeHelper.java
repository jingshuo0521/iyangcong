package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-04.
 * 解密
 */

public interface EncodeHelper {
	/**
	 * author:WuZepeng </br>
	 * time:2017-12-04 10:50 </br>
	 * desc:解密 </br>
	 */
	String encrypMessage(String unEncrypMessage);
	/**
	 * author:WuZepeng </br>
	 * time:2017-12-04 10:50 </br>
	 * desc:解密 </br>
	 */
	String encrypMessage(byte[] unEcrpMessageArray);
}
