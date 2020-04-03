package org.geometerplus.fbreader.formats;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.formats.oeb.function.encryp.AESEncodeAndDecodeHelperImpl;
import org.geometerplus.fbreader.formats.oeb.function.encryp.AESProcessor;
import org.geometerplus.fbreader.formats.oeb.function.encryp.CacheHelper;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DecodeHelper;

import java.util.HashMap;

/**
 * Created by WuZepeng on 2017-12-11.
 * AES解密代理器
 */
public class AESDecodeDelegate implements DecodeHelper, CacheHelper<String, String>, AESProcessor {
	public static final String $_$Tag = "(\\$(.|\n)+?\\$)";
	public static final String SPACE = "";
	public static final String $Tag = "\\$";
	public static final char $Char = '$';
	private static String lastUsdStr = null;
	private static HashMap<String, String> mStringPools = new HashMap<>();

	@Override
	public String decrypt(String decryptMessage) {
		if (TextUtils.isEmpty(decryptMessage)) {
			return "";
		}
		String cachedStr = isCached(decryptMessage);
		if (cachedStr != null) {
			return cachedStr;
		}
		//需要增加对一些特殊情况的处理，比如说decryptMessage只有标记字符，而没有密文字符时，要进行处理；
//		StringBuilder messageBuilder = new StringBuilder();
		String[] decryptMessageByGruop = getPreixMessage(decryptMessage);
		if(decryptMessage.charAt(decryptMessage.length()-1) != $Char){
			saveSuffixMessage(decryptMessageByGruop[decryptMessageByGruop.length-1]);
		}else{
			lastUsdStr = null;
		}
		return handleNormalMessage(decryptMessageByGruop);
//			DecodeHelper tmpDecodeHelper = new AESEncodeAndDecodeHelperImpl();
//			Logger.e("wzp str to decode:%s%n", decryptMessage);
//			if (decryptMessage.charAt(0) != $Char && !TextUtils.isEmpty(lastUsdStr)) {
//				String tmp = messageBuilder.append(decryptMessage).append($Char).toString().replaceAll($_$Tag, SPACE).replaceAll($Tag, SPACE);
//				Logger.e("wzp preix:%s%n ", tmp);
//				messageBuilder.delete(0, messageBuilder.length());
//				messageBuilder.append(lastUsdStr).append(tmp);
//				Logger.e("wzp addLast:%s%n", messageBuilder.toString());
//
//				cachedStr = isCached(messageBuilder.toString());
//				if(cachedStr != null){
//					sb.append(cachedStr);
//				}else{
//					String afterDecrypt = tmpDecodeHelper.decrypt(messageBuilder.toString());
//					cache(messageBuilder.toString(),afterDecrypt);
//					Logger.e("wzp preix:%s%n ", afterDecrypt);
//					sb.append(afterDecrypt);
//					messageBuilder.deleteCharAt(messageBuilder.length() - 1);
//				}
//			}
//			if (decryptMessage.charAt(decryptMessage.length() - 1) != $Char) {
//				if (messageBuilder.length() > 0) {
//					messageBuilder.delete(0, messageBuilder.length());
//				}
//				messageBuilder.append($Char).append(decryptMessage);
//				lastUsdStr = messageBuilder.toString().replaceAll($_$Tag, SPACE).replaceAll($Tag, SPACE);
//				Logger.e("wzp preix:%s%n ", lastUsdStr);
//			}
//
//			Pattern pattern = Pattern.compile($_$Tag);
//			Matcher matcher = pattern.matcher(decryptMessage);
//			for (; matcher.find(); ) {
//				String tmp = matcher.group().replaceAll($Tag, SPACE);
//				Logger.e("wzp every:%s%n", tmp);
//				cachedStr = isCached(tmp);
//				if(null != cachedStr)
//					sb.append(cachedStr);
//				else{
//					String afterDecrypt = tmpDecodeHelper.decrypt(tmp);
//					sb.append(afterDecrypt);
//					cache(tmp,afterDecrypt);
//				}
//			}
		//		int length = 195;
//		StringBuffer sb = new StringBuffer();
//		StringReader tmpStringReader = new StringReader(message);
//		BufferedReader tmpBufferedReader = new BufferedReader(tmpStringReader);
//		char[] buffer = new char[length];
//		DecodeHelper tmpDecodeHelper = new AESEncodeAndDecodeHelperImpl();
//		try {
//			int readCharNum = tmpBufferedReader.read(buffer,0,length);
//			while (readCharNum>0){
//				if(readCharNum>=length){
//					sb.append(tmpDecodeHelper.decrypt(new String(buffer)));
//				}
//				readCharNum = tmpBufferedReader.read(buffer,0,length);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return sb.toString();
	}

	@Override
	public String isCached(String key) {
		String reuslt = mStringPools.get(key);
		if (null != reuslt) {
			return reuslt;
		}
		return null;
	}

//	@Override
//	public StringBuilder getPreixMessage(StringBuilder stringBuilder, String decryptMessage) {
////		Logger.e("wzp str to decode:%s%n", decryptMessage);
//		DecodeHelper tmpDecodeHelper = new AESEncodeAndDecodeHelperImpl();
//		StringBuilder messageBuilder = new StringBuilder();
//		if (decryptMessage.charAt(0) != $Char && !TextUtils.isEmpty(lastUsdStr)) {
//			String tmp = messageBuilder.append(decryptMessage).append($Char).toString().replaceAll($_$Tag, SPACE).replaceAll($Tag, SPACE);
////			Logger.e("wzp preix:%s%n ", tmp);
//			messageBuilder.delete(0, messageBuilder.length());
//			messageBuilder.append(lastUsdStr).append(tmp);
////			Logger.e("wzp addLast:%s%n", messageBuilder.toString());
//
//			String cachedStr = isCached(decryptMessage);
//			if (cachedStr != null) {
//				stringBuilder.append(cachedStr);
//			} else {
//				String afterDecrypt = tmpDecodeHelper.decrypt(messageBuilder.toString());
//				cache(messageBuilder.toString(), afterDecrypt);
//				Logger.e("wzp preix:%s%n ", afterDecrypt);
//				stringBuilder.append(afterDecrypt);
//				messageBuilder.deleteCharAt(messageBuilder.length() - 1);
//			}
//		}
//		return stringBuilder;
//	}

	@Override
	public String[] getPreixMessage(String decryptMessage) {
		if(decryptMessage.charAt(0) != $Char && lastUsdStr != null){
			StringBuilder sb = new StringBuilder();
			sb.append(lastUsdStr).append(decryptMessage);
			return sb.toString().split($Tag);
		}
		return decryptMessage.split($Tag);
	}

	@Override
	public void saveSuffixMessage(String decryptMessage) {
//		StringBuilder messageBuilder = new StringBuilder();
//		if (decryptMessage.charAt(decryptMessage.length() - 1) != $Char) {
//			messageBuilder.append($Char).append(decryptMessage);
//			lastUsdStr = messageBuilder.toString().replaceAll($_$Tag, SPACE).replaceAll($Tag, SPACE);
//			Logger.e("wzp preix:%s%n ", lastUsdStr);
//		}
		if(decryptMessage != null){
			lastUsdStr = decryptMessage;
		}
	}

	@Override
	public String handleNormalMessage(String[] decryptMessage) {
		StringBuffer sb = new StringBuffer();
		DecodeHelper tmpDecodeHelper = new AESEncodeAndDecodeHelperImpl();
		if(decryptMessage == null|| 0 >=decryptMessage.length)
			return null;
		int length = decryptMessage.length;
		if(decryptMessage[length-1].equals(lastUsdStr)){
			length--;
		}
		for (int index = 0;index < length;index++){
			if(TextUtils.isEmpty(decryptMessage[index]))
				continue;
			String result = isCached(decryptMessage[index]);
			if(result != null){
				sb.append(result);
			}else{
				String message = tmpDecodeHelper.decrypt(decryptMessage[index]);
//				Logger.e("wzp 解密以后的内容：%s%n",message);
				sb.append(message);
			}
		}
//		Logger.e("wzp 全部解密以后的内容：%s%n",sb.toString());
		return sb.toString();
	}

//	@Override
//	public StringBuilder handleNormalMessage(StringBuilder stringBuilder, String decryptMessage) {
//		Pattern pattern = Pattern.compile($_$Tag);
//		Matcher matcher = pattern.matcher(decryptMessage);
//		DecodeHelper tmpDecodeHelper = new AESEncodeAndDecodeHelperImpl();
//		for (; matcher.find(); ) {
//			String tmp = matcher.group().replaceAll($Tag, SPACE);
//			Logger.e("wzp every:%s%n", tmp);
//			String cachedStr = isCached(tmp);
//			if (null != cachedStr)
//				stringBuilder.append(cachedStr);
//			else {
//				String afterDecrypt = tmpDecodeHelper.decrypt(tmp);
//				stringBuilder.append(afterDecrypt);
//				cache(tmp, afterDecrypt);
//			}
//		}
//		return stringBuilder;
//	}

	@Override
	public boolean cache(String key, String value) {
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
			return false;
		mStringPools.put(key, value);
		return true;
	}
}
