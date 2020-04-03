package org.geometerplus.fbreader.formats.oeb.function.encryp;

import android.text.TextUtils;

import com.iyangcong.reader.utils.FileHelper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by WuZepeng on 2017-12-06.
 */

public class DESEupbConsumerImpl implements DESEpubConsumer {

	public static final int SIZE = 1024;
	String deviceToken;
	String key;
	public DESEupbConsumerImpl(String deviceToken,String key) {
		this.deviceToken = deviceToken;
		this.key = key;
	}

	public SecretKey genSecretKey(String plaintext) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] md5encoded = md.digest(plaintext.getBytes());
//        byte[] buf = new byte[8];
//        System.arraycopy(md5encoded, 4, buf, 0, 8);
		byte[] buf = new byte[]{-111, 8, -34, -54, -72, 5, -9, 40};
		DESKeySpec desKeySpec = new DESKeySpec(buf);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		return secretKey;
	}

	public byte[] desDecryption(String filePath) throws Exception{
		return desDecryption(filePath,key);
	}

	@Override
	public byte[] desDecryption(String filePath, String sertkey) throws Exception {
		if (TextUtils.isEmpty(sertkey)) {
			return null;
		}
		SecretKey skey = genSecretKey(deviceToken);
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skey);
		byte[] decrypted = cipher.doFinal(hex2byte(sertkey));
		Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		DESKeySpec desSpec = new DESKeySpec(decrypted);
		SecretKey key = skf.generateSecret(desSpec);
		c.init(Cipher.DECRYPT_MODE, key);
		CipherInputStream cis = new CipherInputStream(new FileInputStream(filePath), c);
		return FileHelper.inputToByte( new BufferedInputStream(cis, SIZE));
	}

	public byte[] hex2byte(String hex) {
		String digital = "0123456789abcdef";
		hex = hex.toLowerCase();
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return bytes;
	}
}
