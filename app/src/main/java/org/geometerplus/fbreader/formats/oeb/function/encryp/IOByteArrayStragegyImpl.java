package org.geometerplus.fbreader.formats.oeb.function.encryp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by WuZepeng on 2017-12-19.
 */

public class IOByteArrayStragegyImpl implements ByteArrayStrategy {

	private int mReadLength;

	public IOByteArrayStragegyImpl(int readLength) {
		mReadLength = readLength;
	}

	@Override
	public String transfor(byte[] byteArr) {
		StringBuilder sb = new StringBuilder();
		ByteArrayInputStream reader = new ByteArrayInputStream(byteArr);
		byte[] bufferTmp = new byte[mReadLength];
		int readLength;
		while ((readLength = reader.read(bufferTmp,0,mReadLength))>0){
			if(readLength == mReadLength)
				sb.append(new String(bufferTmp));
			else if(readLength < mReadLength)
				sb.append(new String(Arrays.copyOf(bufferTmp,readLength)));
		}
		if(reader != null){
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
