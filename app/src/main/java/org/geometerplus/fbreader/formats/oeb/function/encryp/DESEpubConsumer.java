package org.geometerplus.fbreader.formats.oeb.function.encryp;

import javax.crypto.SecretKey;

/**
 * Created by WuZepeng on 2017-12-06.
 */

public interface DESEpubConsumer {

	SecretKey genSecretKey(String instr) throws Exception;
	/**
	 * author:WuZepeng </br>
	 * time:2017-12-06 15:20 </br>
	 * desc:对已经通过DES加密的Epub包进行DES解密的代理方法 </br>
	 */
	byte[] desDecryption(String filePath) throws Exception;
	/**
	 * author:WuZepeng </br>
	 * time:2017-12-06 15:20 </br>
	 * desc:对已经通过DES加密的Epub包进行DES解密 </br>
	 */
	byte[] desDecryption(String filePath, String sertkey) throws Exception;
}
