
package org.geometerplus.fbreader.formats.oeb.function;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.MsgBookInfo;
import com.iyangcong.reader.interfaceset.CharSet;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.FileHelper;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.formats.oeb.function.bean.BookChapter;
import org.geometerplus.fbreader.formats.oeb.function.bean.BookInfo;
import org.geometerplus.fbreader.formats.oeb.function.bean.NavMap;
import org.geometerplus.fbreader.formats.oeb.function.encryp.AESEncodeAndDecodeHelperImpl;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEupbConsumerImpl;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EncodeHelper;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 王浩
 * @category 解压解密，获取epub内容
 * @created 2013.5.6 AM 10:16
 */

public class EpubKernel {

	public static final String SERTKEY = "52329717a4b326d373384c6b8b1bb02b";
	private String bookName;

	private static final String TAG = "EpubKernel message:%s%n";

	private Handler handler;

	private String basePath = null;

	// 书压缩文件路径
	private String bookZipPath = null;

	// 书解压后的目录
	private String bookZipedDir = null;

	public static final int READ_EN_BOOK = 0;

	public static final int READ_ZH_BOOK = 1;

	private AppContext appContext;

	private static String deviceToken;

	private boolean containEnBook = false;

	private boolean containZhBook = false;

	public EpubKernel(Context context, String bookName, Handler handler) {
		this.bookName = bookName;
		this.handler = handler;
		appContext = (AppContext) context.getApplicationContext();
		deviceToken = 3 + appContext.getDeviceToken();
		initFilePath();
	}

	public void startParseTask() {
		// 解压成epub文件,然后读取内容
		UnZipTask unZipTask = new UnZipTask(bookZipPath, bookZipedDir, bookName);
		unZipTask.execute();
	}

	private void initFilePath() {
		basePath = CommonUtil.getBooksDir();
		bookZipPath = basePath + bookName;
		bookZipedDir = basePath;
	}




	public boolean isContainEnBook() {
		return containEnBook;
	}

	public void setContainEnBook(boolean containEnBook) {
		this.containEnBook = containEnBook;
	}

	public boolean isContainZhBook() {
		return containZhBook;
	}

	public void setContainZhBook(boolean containZhBook) {
		this.containZhBook = containZhBook;
	}

	public void cleanSDCache() {
		String dir = basePath + bookName.replace(".zip", "");
		boolean isDelete = FileHelper.deleteDirectory(dir);
		if (isDelete) {
			Logger.d(TAG, "删除缓存成功");
		} else {
			Logger.d(TAG, "删除缓存失败");
		}
	}

	//以指定长度分割字符串进行解密
	private String splitString(String str, int length) {
		String result = "";
		for (int i = 0; length * i <= str.length(); i++) {
			EncodeHelper tmpEncodeHelper = new AESEncodeAndDecodeHelperImpl();
			if (length * (i + 1) <= str.length()) {
				result += tmpEncodeHelper.encrypMessage(str.substring(length * i, length * (i + 1)));
			} else {
				result += tmpEncodeHelper.encrypMessage(str.substring(length * i));
			}
		}
		return result;
	}

	/**
	 * 启动线程 用来解密图书，解析图书内容 将复杂耗时的任务交给此线程
	 */
	class UnZipTask extends AsyncTask<Void, Void, Void> {

		// 书压缩文件路径
		private String bookZipPath = "";

		private String bookName = "";


		public UnZipTask(String bookZipPath, String epubZipEnPath, String bookName) {
			this.bookZipPath = bookZipPath;
			this.bookName = bookName;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				DESEpubConsumer tmpConsumer = new DESEupbConsumerImpl(deviceToken,SERTKEY);
				EpubProducer tmpEpubProducer = new EpubProducerImpl();
				MsgBookInfo msgbookinfo = new MsgBookInfo();
				int supportLanguage = 0;
				cleanSDCache();
				// 分别解压连个epub
				String _bookName = bookName.replace(".zip", "");
				// 解压中英压缩包得到2个epub文件
				FileHelper.unzip(bookZipPath, bookZipedDir, _bookName);
				// 获得解压目录中的文件列表
				String zipedPath = bookZipedDir + _bookName + "/";
				Logger.i(TAG, zipedPath);
				File file = new File(zipedPath);
				if (!file.exists()) {
					handler.sendEmptyMessage(6);
					return null;
				}
				if (file.isDirectory()) {
					String[] fileName = file.list();
					for (int i = 0; i < fileName.length; i++) {
						if (fileName[i].contains("en.epub")) {
							containEnBook = true;
							if (supportLanguage == 1) {
								supportLanguage = 3;
								break;
							} else {
								supportLanguage = 2;
							}
							Logger.d(TAG, "包括英文书籍");
						}
						if (fileName[i].contains("zh.epub")) {
							containZhBook = true;
							if (supportLanguage == 2) {
								supportLanguage = 3;
								break;
							} else {
								supportLanguage = 1;
							}
							Logger.d(TAG, "包括中文书籍");
						}
					}
				}
				BookInfo bookInfo = new BookInfo();
				if (containEnBook) {
					com.iyangcong.reader.bean.BookInfo bookIdInfoEn = new com.iyangcong.reader.bean.BookInfo();
					bookIdInfoEn.setBookId(Integer.parseInt(_bookName));
					bookIdInfoEn.setLanguage(2);
					String enEpubDir = bookZipedDir + _bookName + "/" + _bookName + ".en.epub";
					String enZipedPath = bookZipedDir + _bookName + "/" + _bookName + ".en/";
					// 解压epub文件
					FileHelper.unzip(enEpubDir, bookZipedDir + _bookName + "/", _bookName + ".en");
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
					for (int i = 0; i < size; i++) {
						BookChapter bookChapter = new BookChapter();
						// 获得章节路径
						String chapterHref = enManifestMap.get(enSpineList.get(i));

						String chapterPath = enZipedPath + "OEBPS/" + chapterHref;

						String data;
//                        if (_bookName.equals("53")) {
//                            data = new String(FileHelper.readFileByBytes(chapterPath), "UTF-8");
//                        } else {
						data = new String(tmpConsumer.desDecryption(chapterPath, SERTKEY), CharSet.UTF_8);
//						data = new String(FileHelper.inputToByte(EpubKernel.doEncrypt(
//								chapterPath, )), "UTF-8");
//                        }
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
//                        data = data.replaceAll("\n</p>\n", "</p>");

						tmpEpubProducer.produceWithEncryption(chapterPath, data);
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
							int segIdBegin = data.indexOf("<p id=\"");
							String segIdStr = data.substring(segIdBegin + 7);
							int segIdEnd = segIdStr.indexOf("\">");
							String segId = segIdStr.substring(0, segIdEnd);
							int segmengId = Integer.parseInt(segId);
							bookIdInfoEn.setSegmentId(segmengId);
							bookInfo.setMinChapterId(chapterId);
						}
						enChapters.add(bookChapter);
					}
					Gson gson = new Gson();
					String ChapterIds = gson.toJson(chapterIdList);
					String ChapterNames = gson.toJson(chapterNameList);
					bookIdInfoEn.setChapterId(ChapterIds);
					bookIdInfoEn.setEnglishChapterName(ChapterNames);
					msgbookinfo.setBookInfoEn(bookIdInfoEn);

					bookInfo.setEnChaptersList(enChapters);
					FileHelper.zipFile(bookZipedDir + _bookName, _bookName + ".en", enEpubDir);
				}

				if (containZhBook) {
					com.iyangcong.reader.bean.BookInfo bookIdInfoZh = new com.iyangcong.reader.bean.BookInfo();
					bookIdInfoZh.setBookId(Integer.parseInt(_bookName));
					bookIdInfoZh.setLanguage(1);
					String zhEpubDir = bookZipedDir + _bookName + "/" + _bookName + ".zh.epub";
					String zhZipedPath = bookZipedDir + _bookName + "/" + _bookName + ".zh/";
					FileHelper.unzip(zhEpubDir, bookZipedDir + _bookName + "/", _bookName + ".zh");
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

					for (int i = 0; i < size; i++) {
						BookChapter bookChapter = new BookChapter();
						// 获得章节路径
						String chapterHref = zhManifestMap.get(zhSpineList.get(i));
						String chapterPath = zhZipedPath + "OEBPS/" + chapterHref;
						Logger.d("ljw: chapterPath=" + chapterPath);

						String data;
//						byte[] bytes =
//								FileHelper.inputToByte(EpubKernel.doEncrypt(
//								chapterPath, "52329717a4b326d373384c6b8b1bb02b"));
						data = new String(tmpConsumer.desDecryption(chapterPath,SERTKEY), CharSet.UTF_8);
						data = data.replaceAll("<font face=楷体_GB2312>", "");
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

						tmpEpubProducer.produceWithEncryption(chapterPath, data);

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
							int segIdBegin = data.indexOf("<p id=\"");
							String segIdStr = data.substring(segIdBegin + 7);
							int segIdEnd = segIdStr.indexOf("\">");
							String segId = segIdStr.substring(0, segIdEnd);
							int segmengId = Integer.parseInt(segId);
							bookIdInfoZh.setSegmentId(segmengId);
							bookInfo.setMinChapterId(chapterId);
						}
						//bookChapter.setChapterId(i);
						zhChapters.add(bookChapter);
					}
					Gson gson = new Gson();
					String ChapterIds = gson.toJson(chapterIdList);
					String ChapterNames = gson.toJson(chapterNameList);
					bookIdInfoZh.setChapterId(ChapterIds);
					bookIdInfoZh.setEnglishChapterName(ChapterNames);
					msgbookinfo.setBookInfoZh(bookIdInfoZh);

					bookInfo.setZhChaptersList(zhChapters);
					FileHelper.zipFile(bookZipedDir + _bookName, _bookName + ".zh", zhEpubDir);
				}
				msgbookinfo.setBookId(Integer.parseInt(_bookName));
				Message message = new Message();
				message.what = supportLanguage;
				message.obj = msgbookinfo;
				handler.sendMessage(message);
				// TODO: 2017/5/15
				switch (supportLanguage) {
					case 1:
						FileHelper.deleteDirectory(bookZipedDir + _bookName + "/" + _bookName + ".zh");
						break;
					case 2:
						FileHelper.deleteDirectory(bookZipedDir + _bookName + "/" + _bookName + ".en");
						break;
					case 3:
						FileHelper.deleteDirectory(bookZipedDir + _bookName + "/" + _bookName + ".zh");
						FileHelper.deleteDirectory(bookZipedDir + _bookName + "/" + _bookName + ".en");
						break;
				}
				FileHelper.deleteDirectory(bookZipPath);
			} catch (Exception e) {
				handler.sendEmptyMessage(6);
				return null;
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Logger.v(TAG, "starting parse epub......");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Logger.v(TAG, " epub parse finished......");
		}
	}
}
