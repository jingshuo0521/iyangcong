package com.iyangcong.reader.epub;

import com.iyangcong.reader.bean.BookInfo;

import org.geometerplus.fbreader.formats.oeb.function.encryp.ClearCache;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;

import java.io.IOException;

/**
 * Created by WuZepeng on 2018-01-03.
 */

public interface EpubProcessor extends ClearCache {
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 9:17 </br>
	 * desc:解压文件 </br>
	 * method_params: [originalPath	从后台下载下来的epub的路径, destination	解压完以后所在的路径, fileName	解压完以后的文件名] </br>
	 */
	void unZipFile(String originalPath,String destination,String fileName) throws IOException;
	/**
	 * 获取语言版本，如果只有一种语言版本的话只需要处理一种情况，
	 * 如果有两种语言版本的话需要对两种语言版本都进行处理
	 * method_params: [filePath 解压完的epub路径] </br>
	 */
	int getLangaugeType(String filePath);
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:12 </br>
	 * desc:对每章进行格式处理、DES加密和AES解密 </br>
	 */
	void handleSubEpubBylanguageType(String destinationPath, long bookId, int language, DESEpubConsumer consumer, EpubProducer epubProducer) throws Exception;
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:13 </br>
	 * desc:将书籍信息保存到bookInfo表 </br>
	 */
	void saveInfo(BookInfo info);
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:13 </br>
	 * desc:判断某本书是否已经解密过并且生成了Epub文件 </br>
	 */
	boolean hasEpub(String desKey, long bookId);
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:14 </br>
	 * desc:保存加密解密的记录 </br>
	 */
	void saveEpubEncryInfo(int infoType, long bookId, int languageType);
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:14 </br>
	 * desc:删除源文件 </br>
	 */
	boolean deleteOriginFile(String path);
	/**
	 * author:WuZepeng </br>
	 * time:2018-01-03 16:14 </br>
	 * desc:完成时的回掉，用来发送EventBus事件，告诉相关页面，解密工作已经完成 </br>
	 */
	void finish(boolean isSuccess,long bookId,String message);
}
