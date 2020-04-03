package com.iyangcong.reader.epub;

import android.util.Config;

import com.google.gson.Gson;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.interfaceset.CharSet;
import com.iyangcong.reader.interfaceset.LanguageType;
import com.iyangcong.reader.utils.FileHelper;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.formats.oeb.function.EpubXmlParser;
import org.geometerplus.fbreader.formats.oeb.function.bean.BookChapter;
import org.geometerplus.fbreader.formats.oeb.function.bean.NavMap;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WuZepeng on 2018-01-03.
 */

public class EnEpubProcessor implements EpubLanguageTypeProcessor{

	@Override
	public String encodeAndDecode(String chapterPath, DESEpubConsumer consumer, EpubProducer producer) throws Exception {
		String data = new String(consumer.desDecryption(chapterPath), CharSet.UTF_8);
		data = data.replace("<html>\n" +
				"<head>\n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n" +
				"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
				"</head>", "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
				"<head>\n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/>\n" +
				"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
				"</head>");
		data = data.replaceAll("<font.*?>", "");
		data = data.replaceAll("</font>", "");
//		producer.produceWithEncryption(chapterPath, data);
//		if(Config.DEBUG)
//			producer.produceWithOutEncrption(chapterPath,data);
//		else
			producer.produceWithEncryption(chapterPath, data);
		return data;
	}

	@Override
	public BookInfo getBookInfoFromEpub(String destinationPath, long bookId, DESEpubConsumer consumer, EpubProducer producer) throws Exception{
		String enEpubDir = destinationPath + bookId + "/" + bookId + ".en.epub";
		String enZipedPath = destinationPath + bookId + "/" + bookId + ".en/";
		FileHelper.unzip(enEpubDir, destinationPath + bookId + "/", bookId + ".en");
		// 解压epub文件
		// 解析manifest
		Map<String, String> enManifestMap = EpubXmlParser
				.parseManifest(new FileInputStream(enZipedPath + "OEBPS/content.opf"));
		// 解析spine
		List<String> enSpineList = EpubXmlParser.parseSpine(new FileInputStream(
				enZipedPath + "OEBPS/content.opf"));
		// 解析toc.ncx
		List<NavMap> enTocList = EpubXmlParser.parseNcx(new FileInputStream(enZipedPath
				+ "OEBPS/toc.ncx"));
		List<BookChapter> enChapters = new ArrayList<BookChapter>();
		int size = enSpineList.size();
		int[] chapterIdList = new int[size];
		String[] chapterNameList = new String[size];

		BookInfo bookIdInfoEn = new BookInfo();
		bookIdInfoEn.setBookId(bookId);
		bookIdInfoEn.setLanguage(LanguageType.EN);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			BookChapter bookChapter = new BookChapter();
			// 获得章节路径
			String chapterHref = enManifestMap.get(enSpineList.get(i));
			String chapterPath = enZipedPath + "OEBPS/" + chapterHref;
			String data = encodeAndDecode(chapterPath,consumer,producer);
			bookChapter.setChapterSrc("file:///" + chapterPath);
			// 解析名称
			bookChapter.setChapterName(enTocList.get(i).getName());
			chapterNameList[i] = enTocList.get(i).getName();
			// 解析章节id
			int beginIndex = chapterHref.indexOf(".") + 1;
			int endIndex = chapterHref.lastIndexOf(".");
			String strChapterId = chapterHref.substring(beginIndex, endIndex);
			int chapterId = Integer.parseInt(strChapterId);
			//bookChapter.setChapterId(i);
			bookChapter.setChapterId(chapterId);
			chapterIdList[i] = chapterId;
			if (i == 0) {
//				int segIdBegin = data.indexOf("<p id=\"");
//				String segIdStr = data.substring(segIdBegin + 7);
//				int segIdEnd = segIdStr.indexOf("\">");
//				String segId = segIdStr.substring(0, segIdEnd);
//				Logger.e("wzp segId:%s%n",segId);
				Pattern pidPattern = Pattern.compile("<p id=\"\\d*?\"");
				Matcher pidMather = pidPattern.matcher(data);
				if(pidMather.find()){
					Logger.e("wzp 解析段落Id:"+pidMather.group());
					Pattern numberPattern = Pattern.compile("(\"\\d+\")");
					Matcher numberMatcher = numberPattern.matcher(pidMather.group());
					if(numberMatcher.find()){
						Logger.e("wzp paragraphId:%s%n",numberMatcher.group());
						int segmengId = Integer.parseInt(numberMatcher.group().replaceAll("\"",""));
						bookIdInfoEn.setSegmentId(segmengId);
					}
				}
			}
			enChapters.add(bookChapter);
		}
		long endTime = System.currentTimeMillis();
		long result = endTime - startTime;
		Logger.e("wzp 加密开始的时间:%d%n加密结束的时间:%d%n消耗时间：%d%n",startTime,endTime,result);
		Gson gson = new Gson();
		String ChapterIds = gson.toJson(chapterIdList);
		String ChapterNames = gson.toJson(chapterNameList);
		bookIdInfoEn.setChapterId(ChapterIds);
		bookIdInfoEn.setEnglishChapterName(ChapterNames);
		FileHelper.zipFile(destinationPath + bookId, bookId + ".en", enEpubDir);
		deleteZipFile(destinationPath + bookId + "/" + bookId + ".en");
		return bookIdInfoEn;
	}

	@Override
	public boolean deleteZipFile(final String path) {
		Timer tmpTimer = new Timer();
		tmpTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				FileHelper.deleteDirectory(path);
			}
		},2000);
		return true;
	}
}
