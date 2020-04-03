package org.geometerplus.fbreader.formats.oeb.function.encryp;

import com.iyangcong.reader.interfaceset.CharSet;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class AESKernel {
    /**
     * 在java中调用sun公司提供的3DES加密解密算法时，需要使用到$JAVA_HOME/jre/lib/目录下如下的4个jar包：
     * jce.jar security/US_export_policy.jar 　　 security/local_policy.jar
     * ext/sunjce_provider.jar
     */

	/** 默认密钥，实际项目中可配置注入或数据库读取 */
	private static String defaultKey = "hsylgwk-20120101";
	private static final String CipherMode ="AES/CBC/PKCS5Padding";
	private static final String EBCMode = "AES/ECB/PKCS5Padding";
//	private static final String EBCMode = "AES";
	private static final String IVKey = "1234567890123456";//偏移向量
	/** 加密工具 */
	private Cipher encryptCipher = null;

	/** 解密工具 */
	private Cipher decryptCipher = null;

	private static AESKernel instance = new AESKernel(defaultKey);

	public static AESKernel getInstance() {
		return instance;
	}

	private AESKernel(String keyvalue){
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		if(keyvalue==null)
			keyvalue=defaultKey;
		byte[] arrBTmp=null;
		try {
			arrBTmp = keyvalue.getBytes(CharSet.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 创建一个空的length位字节数组（默认值为0）
		byte[] arrB = new byte[16];

		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "AES");

		// 生成Cipher对象,指定其支持的AES算法
		try {
			encryptCipher = Cipher.getInstance(EBCMode);
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
//			encryptCipher.init(Cipher.ENCRYPT_MODE, key,createIV(IVKey));

			decryptCipher = Cipher.getInstance(EBCMode);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
//			decryptCipher.init(Cipher.DECRYPT_MODE, key,createIV(IVKey));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
//		catch (InvalidAlgorithmParameterException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * author:WuZepeng </br>
	 * time:2017-12-22 13:54 </br>
	 * desc:生成加密解密时的偏移向量 </br>
	 */
	private static IvParameterSpec createIV(String password) {
		byte[] data = null;
		if (password == null) {
			password ="";
		}
		StringBuffer sb = new StringBuffer(16);
		sb.append(password);
		while (sb.length() < 16) {
			sb.append("0");
		}
		if (sb.length() > 16) {
			sb.setLength(16);
		}

		try {
			data = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new IvParameterSpec(data);
	}
	/**
	 * 对字符串加密
	 * 
	 * @param str
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Encrytor(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] src = null;
		try {
			src = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptCipher.doFinal(src);
	}
	/**
	 * 对字符串加密
	 *
	 * @param src
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Encrytor(byte[] src) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if(src != null && src.length>0)
			return encryptCipher.doFinal(src);
		return src;
	}


	/**
	 * 对字符串解密
	 * 
	 * @param buff
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Decryptor(byte[] buff) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return decryptCipher.doFinal(buff);
	}

	/**
	 * @param args
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 */
	public static void main(String[] args) throws Exception {
		AESKernel de1 = AESKernel.getInstance();
		//String msg = "郭德纲-搞笑相声全集";
		String msg = "6bc1bee22e409f96e93d7e117393172a";
		byte[] encontent = de1.Encrytor(msg);
		byte[] decontent = de1.Decryptor(encontent);
//		OutputStream out = new FileOutputStream("D:\\AES\\2.txt");
	//	out.write(encontent);
		//out.close();
//		InputStream in = new FileInputStream("D:\\AES\\2.txt");
//		byte[] ib = new byte[in.available()];
//		in.read(ib);
//		byte[] deib = de1.Decryptor(ib);
	}
}