package com.iyangcong.reader.epub;

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

public class ZhEpubProcessor implements EpubLanguageTypeProcessor {

	@Override
	public BookInfo getBookInfoFromEpub(String destinationPath, long bookId, DESEpubConsumer consumer, EpubProducer epubProducer) throws Exception {
		com.iyangcong.reader.bean.BookInfo bookIdInfoZh = new com.iyangcong.reader.bean.BookInfo();
		bookIdInfoZh.setBookId(bookId);
		bookIdInfoZh.setLanguage(LanguageType.ZH);
		String zhEpubDir = destinationPath + bookId + "/" + bookId + ".zh.epub";
		String zhZipedPath = destinationPath + bookId + "/" + bookId + ".zh/";
		FileHelper.unzip(zhEpubDir, destinationPath + bookId + "/", bookId + ".zh");
		// 解析manifest
		Map<String, String> zhManifestMap = EpubXmlParser
				.parseManifest(new FileInputStream(zhZipedPath + "OEBPS/content.opf"));
		// 解析spine
		List<String> zhSpineList = EpubXmlParser.parseSpine(new FileInputStream(
				zhZipedPath + "OEBPS/content.opf"));
		// 解析toc.ncx
		List<NavMap> zhTocList = EpubXmlParser.parseNcx(new FileInputStream(zhZipedPath
				+ "OEBPS/toc.ncx"));
		List<BookChapter> zhChapters = new ArrayList<BookChapter>();
		int size = zhSpineList.size();
		int[] chapterIdList = new int[size];
		String[] chapterNameList = new String[size];
		/**
		 * 将中文epub中content.opf中的语言版本由"en"改为"zh"
		 */
		String content_opf = new String(FileHelper.inputToByte(FileHelper.readFile(zhZipedPath + "OEBPS/content.opf")));
		content_opf = content_opf.replaceAll("<dc:language>en</dc:language>", "<dc:language>zh</dc:language>");
		FileHelper.writeFile(zhZipedPath + "OEBPS/content.opf", content_opf);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			BookChapter bookChapter = new BookChapter();
			// 获得章节路径
			String chapterHref = zhManifestMap.get(zhSpineList.get(i));
			String chapterPath = zhZipedPath + "OEBPS/" + chapterHref;
			Logger.d("wzp: chapterPath：%s%n" + chapterPath);
			String data = encodeAndDecode(chapterPath,consumer,epubProducer);
			bookChapter.setChapterSrc("file:///" + chapterPath);
			// 解析名称
			bookChapter.setChapterName(zhTocList.get(i).getName());
			chapterNameList[i] = zhTocList.get(i).getName();
			// 解析章节id
			int beginIndex = chapterHref.indexOf(".") + 1;
			int endIndex = chapterHref.lastIndexOf(".");
			String strChapterId = chapterHref.substring(beginIndex, endIndex);
			int chapterId = Integer.parseInt(strChapterId);
			chapterIdList[i] = chapterId;
			bookChapter.setChapterId(chapterId);
			if (i == 0) {
//				int segIdBegin = data.indexOf("<p id=\"");
//				String segIdStr = data.substring(segIdBegin + 7);
//				int segIdEnd = segIdStr.indexOf("\">");
//				String segId = segIdStr.substring(0, segIdEnd);
//				int segmengId = Integer.parseInt(segId);
//				bookIdInfoZh.setSegmentId(segmengId);
				Pattern pidPattern = Pattern.compile("<p id=\"\\d*?\"");
				Matcher pidMather = pidPattern.matcher(data);
				if(pidMather.find()){
					Logger.e("wzp 解析段落Id:"+pidMather.group());
					Pattern numberPattern = Pattern.compile("(\"\\d+\")");
					Matcher numberMatcher = numberPattern.matcher(pidMather.group());
					if(numberMatcher.find()){
						Logger.e("wzp paragraphId:%s%n",numberMatcher.group());
						int segmengId = Integer.parseInt(numberMatcher.group().replaceAll("\"",""));
						bookIdInfoZh.setSegmentId(segmengId);
					}
				}
			}
			//bookChapter.setChapterId(i);
			zhChapters.add(bookChapter);
		}
		long endTime = System.currentTimeMillis();
		long result = endTime - startTime;
		Logger.e("wzp 加密开始的时间:%d%n加密结束的时间:%d%n消耗时间：%d%n",startTime,endTime,result);
		Gson gson = new Gson();
		String ChapterIds = gson.toJson(chapterIdList);
		String ChapterNames = gson.toJson(chapterNameList);
		bookIdInfoZh.setChapterId(ChapterIds);
		bookIdInfoZh.setChineseChapterName(ChapterNames);
		FileHelper.zipFile(destinationPath + bookId, bookId + ".zh", zhEpubDir);
		deleteZipFile(destinationPath + bookId + "/" + bookId + ".zh");
		return bookIdInfoZh;
	}

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
//		producer.produceWithOutEncrption(chapterPath, data);
		producer.produceWithEncryption(chapterPath, data);
		return data;
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
