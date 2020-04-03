package org.geometerplus.fbreader.formats.oeb.function.encryp;

import android.util.Log;

import com.iyangcong.reader.interfaceset.CharSet;
import com.iyangcong.reader.utils.FileHelper;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.StringReader;

/**
 * Created by WuZepeng on 2017-12-05.
 */

public class EpubProducerImpl implements EpubProducer {

	private String mBeginEnpcryTags = "<start>";
	private String mEndEnpcryTags = "</start>";

	@Override
	public void produceWithEncryption(String path, String data) {
		if (data == null || data.equals("")) {
			return;
		}
		StringBuilder encryptStr = new StringBuilder("");

		EncodeHelper encodeHelper = new AESEncodeAndDecodeHelperImpl();
		try {
			int times = 0;
			FileHelper.writeFile(path, mBeginEnpcryTags, false);//覆盖以前加密过的内容
//			Logger.e("wzp 分组加密之前的明文：%s%n",data);
//			ByteArrayInputStream tmpOutputStream = new ByteArrayInputStream(data.getBytes(CharSet.UTF_8));
			char[] buffer = new char[mGroupLength];
			StringReader tmpStringReader = new StringReader(data);
//			CharArrayReader tmpCharArrayReader = new CharArrayReader(data.toCharArray());
//			BufferedReader reader = new BufferedReader(tmpStringReader);
//			Logger.e("wzp utf-8:%s%n",new String(data.getBytes(CharSet.UTF_8)));
			String messageByGroup = null;
			int actualReadLength = 0;
			while ((actualReadLength=tmpStringReader.read(buffer, 0, mGroupLength))>0) {
//			while (true) {
//				messageByGroup = reader.readLine();
//				if(messageByGroup == null)
//					break;
				messageByGroup = new String(buffer);
				if(actualReadLength <mGroupLength){
					encryptStr.append("$").append(encodeHelper.encrypMessage(messageByGroup.substring(0,actualReadLength))).append("$");
				}else{
					encryptStr.append("$").append(encodeHelper.encrypMessage(messageByGroup)).append("$");
				}
//				encryptStr.append("$").append(encodeHelper.encrypMessage(messageByGroup)).append("$");
//				Logger.e("wzp 次数：%d%n,加密的明文：%s%n,加密的明文的长度：%d%n,明文数组长度：%d%n,加密过的：%s", ++times,messageByGroup,messageByGroup.length(),messageByGroup.getBytes().length, encryptStr.toString());
				if (!encryptStr.toString().equals("")) {
					FileHelper.writeFile(path, encryptStr.toString(), true);
				}
				encryptStr.delete(0, encryptStr.length());
//				for(byte tmp:buffer)
//					encryptStr.append(tmp).append(",");
//				Logger.e("wzp 明文的数组内容：%s%n",encryptStr.toString());
//				encryptStr.delete(0,encryptStr.length());
			}
			FileHelper.writeFile(path, mEndEnpcryTags, true);
//			if(reader != null){
//				reader.close();
//			}
			if(tmpStringReader != null){
				tmpStringReader.close();
			}
//			if(tmpCharArrayReader != null){
//				tmpCharArrayReader.close();
//			}
//			if(tmpOutputStream != null){
//				tmpOutputStream.close();
//			}
		} catch (Exception e) {
			Log.e("wzp 加密异常:%s%n", e.getMessage());
		}
	}

	@Override
	public void produceWithOutEncrption(String path, String data) {
		if (data == null || data.equals("")) {
			return;
		}
		Logger.e("明文：%s",data);
		StringBuilder encryptStr = new StringBuilder("");
		int length = mGroupLength;
		char[] buffer = new char[length];
		StringReader tmpStringReader = new StringReader(data);
		try {
			FileHelper.writeFile(path, "\n", false);
			int actualLength = 0;
			while ((actualLength =tmpStringReader.read(buffer, 0, length))>0) {
				encryptStr.append(new String(buffer));

				if (!encryptStr.toString().equals("")) {
					if(actualLength <length){
						Logger.e("不加密直接写入文件最后一段：%s",encryptStr.toString().substring(0,actualLength));
						FileHelper.writeFile(path, encryptStr.toString().substring(0,actualLength), true);
					}else{
						Logger.e("不加密直接写入文件：%s",encryptStr.toString());
						FileHelper.writeFile(path, encryptStr.toString(), true);
					}
				}
				encryptStr.delete(0, encryptStr.length());
			}
//			FileHelper.writeFile(path, mEndEnpcryTags, true);
		} catch (Exception e) {
			Log.e("wzp 写入文件异常:%s%n", e.getMessage());
		}finally {
			if(tmpStringReader != null){
				try {
					tmpStringReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
