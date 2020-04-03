package org.geometerplus.fbreader.formats.oeb.function.encryp;



import android.util.Base64;

import com.iyangcong.reader.interfaceset.CharSet;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * Created by WuZepeng on 2017-12-04.
 * AES解密器
 */

public class AESEncodeAndDecodeHelperImpl implements EncodeHelper,DecodeHelper {


	private static final int mReadLength = 64;
	private static final int mGroupSize = 128;
	private int decodeStyle;

	public AESEncodeAndDecodeHelperImpl() {
		this(Base64.NO_PADDING);
	}

	public AESEncodeAndDecodeHelperImpl(int decodeStyle) {
		this.decodeStyle = decodeStyle;
	}

	@Override
	public String encrypMessage(byte[] unEcrpMessageArray) {
		AESKernel encryp = AESKernel.getInstance();
		String outputStr = null;
		try {
			//对原文加密
			byte[] encVal = encryp.Encrytor(unEcrpMessageArray);
			outputStr = Base64.encodeToString(encVal, decodeStyle);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		//组装加密后的内容
		return outputStr;
	}

	@Override
	public String encrypMessage(String unEncrypMessage) {
		byte[] src = null;
		try {
			src = unEncrypMessage.getBytes(CharSet.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encrypMessage(src);
	}

	@Override
	public String decrypt(String encrypedMessage) {
		AESKernel encryp = AESKernel.getInstance();
		StringBuffer outputStr = new StringBuffer("");
		try {
//			byte[] tmpBytes = HexTransfer.hexStr2ByteArr(encrypedMessage);
//			Logger.e("wzp 转换："+tmpBytes);
			//对原文解密
			byte[] tmpBytes = Base64.decode(encrypedMessage.getBytes(CharSet.UTF_8),decodeStyle);
			byte[] decValue = encryp.Decryptor(tmpBytes);
			ByteArrayStrategy tmpStrategy =
//					decValue.length >= mGroupSize ?
//					new IOByteArrayStragegyImpl(mReadLength):
					new DirectByteArrayStrategyImpl();
			outputStr.append(tmpStrategy.transfor(decValue));
//			outputStr = new String(Base64.decode(encrpiedStr,decodeStyle));
//			Logger.e("wzp 解密前的密文：%s%n解密后:%s%n",encrypedMessage,outputStr.toString());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		//组装加密后的内容
		return outputStr.toString();
	}
}
