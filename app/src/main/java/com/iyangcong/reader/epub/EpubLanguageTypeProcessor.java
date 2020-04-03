package com.iyangcong.reader.epub;

import com.iyangcong.reader.bean.BookInfo;

import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;

/**
 * Created by WuZepeng on 2018-01-03.
 */

public interface EpubLanguageTypeProcessor {
	BookInfo getBookInfoFromEpub(String destinationPath, long bookId, DESEpubConsumer consumer, EpubProducer epubProducer) throws Exception;
	String encodeAndDecode(String chapterPath, DESEpubConsumer consumer, EpubProducer producer) throws Exception;
	boolean deleteZipFile(String path);
}
