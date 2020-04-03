package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-05.
 * epub生产者
 */

public interface EpubProducer {
	int mGroupLength = 512;
	void produceWithEncryption(String path,String data);
	void produceWithOutEncrption(String path,String data);
}
