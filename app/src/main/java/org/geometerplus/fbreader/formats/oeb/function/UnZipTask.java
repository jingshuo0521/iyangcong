package org.geometerplus.fbreader.formats.oeb.function;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.iyangcong.reader.app.AppContext;

import com.iyangcong.reader.epub.EpubProcessor;
import com.iyangcong.reader.epub.EpubProcessorImpl;
import com.iyangcong.reader.interfaceset.EpubState;
import com.orhanobut.logger.Logger;


import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEupbConsumerImpl;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducerImpl;



/**
 * 启动线程 用来解密图书，解析图书内容 将复杂耗时的任务交给此线程
 */
class UnZipTask extends AsyncTask<Void, Void, Void>{

	private static final String TAG = "UnZipTask message:%s%n";
	private static final String SERTKEY = "52329717a4b326d373384c6b8b1bb02b";
	// 书压缩文件路径
	private String originalUnZipedPath;
	private String destinationPath;
	protected long mBookId;
	private Context mContext;
	private String mDeviceToken;
	protected boolean isSuccessful;
	EpubProcessor mProcessor;
	protected String mMessage;

	public UnZipTask(Context context, String origalPath, String destinationPath, long bookId) {
		this.originalUnZipedPath = origalPath;
		this.destinationPath = destinationPath;
		this.mBookId = bookId;
		mContext = context;
		mDeviceToken = 3 + ((AppContext) context).getDeviceToken();
		mProcessor = new EpubProcessorImpl(mContext);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if(mProcessor.hasEpub(SERTKEY,mBookId)){
			Logger.e("wzp 已经加密过了");
			return null;
		}
		int supportLanguage = -1;
		try {
			DESEpubConsumer tmpConsumer = new DESEupbConsumerImpl(mDeviceToken,SERTKEY);
			EpubProducer tmpEpubProducer = new EpubProducerImpl();
			mProcessor.clearCache(destinationPath + mBookId);
//			MsgBookInfo msgbookinfo = new MsgBookInfo();
			// 分别解压连个epub
//			String _bookName = mBookId + "";
			// 解压中英压缩包得到2个epub文件
//			Logger.e("wzp originalUnZipedPath:%s%ndestinationPath:%s%n_bookName:%s%n", originalUnZipedPath, destinationPath, _bookName);
			mProcessor.unZipFile(originalUnZipedPath, destinationPath, mBookId + "");
			// 获得解压目录中的文件列表
			supportLanguage = mProcessor.getLangaugeType(destinationPath + mBookId + "/");
			Logger.e("Wzp language:%d%n",supportLanguage);
			mProcessor.saveEpubEncryInfo(EpubState.Download,mBookId,supportLanguage);
			mProcessor.handleSubEpubBylanguageType(destinationPath,mBookId,supportLanguage,tmpConsumer,tmpEpubProducer);
			boolean isSuccessfullyDeleted = mProcessor.deleteOriginFile(originalUnZipedPath);
			Logger.e("wzp 删除是否成功：%s%n",isSuccessfullyDeleted+"");
			isSuccessful = true;
//			String zipedPath = destinationPath + _bookName + "/";
//			Logger.i(TAG, zipedPath);
//			File file = new File(zipedPath);
//			if (!file.exists()) {
//				return null;
//			}
//			if (file.isDirectory()) {
//				String[] fileName = file.list();
//				for (int i = 0; i < fileName.length; i++) {
//					if (fileName[i].contains("en.epub")) {
//						containEnBook = true;
//						if (supportLanguage == LanguageType.ZH) {
//							supportLanguage = LanguageType.ZH_AND_EN;
//							break;
//						} else {
//							supportLanguage = LanguageType.EN;
//						}
//						Logger.d(TAG, "包括英文书籍");
//					}
//					if (fileName[i].contains("zh.epub")) {
//						containZhBook = true;
//						if (supportLanguage == LanguageType.EN) {
//							supportLanguage = LanguageType.ZH_AND_EN;
//							break;
//						} else {
//							supportLanguage = LanguageType.ZH;
//						}
//						Logger.d(TAG, "包括中文书籍");
//					}
//				}
//			}
//			BookInfo bookInfo = new BookInfo();
//			if (containEnBook) {
//				com.iyangcong.reader.bean.BookInfo bookIdInfoEn = new com.iyangcong.reader.bean.BookInfo();
//				bookIdInfoEn.setBookId(Integer.parseInt(_bookName));//这里以后可能会是一个隐患，当bookId超过整数范围以后；
//				bookIdInfoEn.setLanguage(LanguageType.EN);
//				String enEpubDir = destinationPath + _bookName + "/" + _bookName + ".en.epub";
//				String enZipedPath = destinationPath + _bookName + "/" + _bookName + ".en/";
//				// 解压epub文件
//				FileHelper.unzip(enEpubDir, destinationPath + _bookName + "/", _bookName + ".en");
//				// 解析manifest
//				Map<String, String> enManifestMap = EpubXmlParser
//						.parseManifest(new FileInputStream(enZipedPath + "OEBPS/content.opf"));
//				// 解析spine
//				List<String> enSpineList = EpubXmlParser.parseSpine(new FileInputStream(
//						enZipedPath + "OEBPS/content.opf"));
//				// 解析toc.ncx
//				List<NavMap> enTocList = EpubXmlParser.parseNcx(new FileInputStream(enZipedPath
//						+ "OEBPS/toc.ncx"));
//				List<BookChapter> enChapters = new ArrayList<BookChapter>();
//				int size = enSpineList.size();
//				int[] chapterIdList = new int[size];
//				String[] chapterNameList = new String[size];
//				for (int i = 0; i < size; i++) {
//					BookChapter bookChapter = new BookChapter();
//					// 获得章节路径
//					String chapterHref = enManifestMap.get(enSpineList.get(i));
//
//					String chapterPath = enZipedPath + "OEBPS/" + chapterHref;
//
//					String data;
////                        if (_bookName.equals("53")) {
////                            data = new String(FileHelper.readFileByBytes(chapterPath), "UTF-8");
////                        } else {
//					data = new String(tmpConsumer.desDecryption(chapterPath, SERTKEY), CharSet.UTF_8);
////						data = new String(FileHelper.inputToByte(EpubKernel.doEncrypt(
////								chapterPath, )), "UTF-8");
////                        }
//					data = data.replace("<html>\n" +
//							"<head>\n" +
//							"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n" +
//							"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
//							"</head>", "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
//							"<head>\n" +
//							"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/>\n" +
//							"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
//							"</head>");
//					data = data.replaceAll("<font.*?>", "");
//					data = data.replaceAll("</font>", "");
////                        data = data.replaceAll("\n</p>\n", "</p>");
//
//					tmpEpubProducer.produceWithEncryption(chapterPath, data);
//					bookChapter.setChapterSrc("file:///" + chapterPath);
//					// 解析名称
//					bookChapter.setEnglishChapterName(enTocList.get(i).getName());
//					chapterNameList[i] = enTocList.get(i).getName();
//					// 解析章节id
//					int beginIndex = chapterHref.indexOf(".") + 1;
//					int endIndex = chapterHref.lastIndexOf(".");
//					String strChapterId = chapterHref.substring(beginIndex, endIndex);
//					int chapterId = Integer.parseInt(strChapterId);
//					//bookChapter.setChapterId(i);
//					bookChapter.setChapterId(chapterId);
//					chapterIdList[i] = chapterId;
//					if (i == 0) {
//						int segIdBegin = data.indexOf("<p id=\"");
//						String segIdStr = data.substring(segIdBegin + 7);
//						int segIdEnd = segIdStr.indexOf("\">");
//						String segId = segIdStr.substring(0, segIdEnd);
//						int segmengId = Integer.parseInt(segId);
//						bookIdInfoEn.setSegmentId(segmengId);
//						bookInfo.setMinChapterId(chapterId);
//					}
//					enChapters.add(bookChapter);
//				}
//				Gson gson = new Gson();
//				String ChapterIds = gson.toJson(chapterIdList);
//				String ChapterNames = gson.toJson(chapterNameList);
//				bookIdInfoEn.setChapterId(ChapterIds);
//				bookIdInfoEn.setEnglishChapterName(ChapterNames);
//				msgbookinfo.setBookInfoEn(bookIdInfoEn);
//
//				bookInfo.setEnChaptersList(enChapters);
//				FileHelper.zipFile(destinationPath + _bookName, _bookName + ".en", enEpubDir);
//			}
//
//			if (containZhBook) {
//				com.iyangcong.reader.bean.BookInfo bookIdInfoZh = new com.iyangcong.reader.bean.BookInfo();
//				bookIdInfoZh.setBookId(Integer.parseInt(_bookName));
//				bookIdInfoZh.setLanguage(LanguageType.ZH);
//				String zhEpubDir = destinationPath + _bookName + "/" + _bookName + ".zh.epub";
//				String zhZipedPath = destinationPath + _bookName + "/" + _bookName + ".zh/";
//				FileHelper.unzip(zhEpubDir, destinationPath + _bookName + "/", _bookName + ".zh");
//				// 解析manifest
//				Map<String, String> zhManifestMap = EpubXmlParser
//						.parseManifest(new FileInputStream(zhZipedPath + "OEBPS/content.opf"));
//				// 解析spine
//				List<String> zhSpineList = EpubXmlParser.parseSpine(new FileInputStream(
//						zhZipedPath + "OEBPS/content.opf"));
//				// 解析toc.ncx
//				List<NavMap> zhTocList = EpubXmlParser.parseNcx(new FileInputStream(zhZipedPath
//						+ "OEBPS/toc.ncx"));
//				List<BookChapter> zhChapters = new ArrayList<BookChapter>();
//				int size = zhSpineList.size();
//				int[] chapterIdList = new int[size];
//				String[] chapterNameList = new String[size];
//				/**
//				 * 将中文epub中content.opf中的语言版本由"en"改为"zh"
//				 */
//				String content_opf = new String(FileHelper.inputToByte(FileHelper.readFile(zhZipedPath + "OEBPS/content.opf")));
//				content_opf = content_opf.replaceAll("<dc:language>en</dc:language>", "<dc:language>zh</dc:language>");
//				FileHelper.writeFile(zhZipedPath + "OEBPS/content.opf", content_opf);
//
//				for (int i = 0; i < size; i++) {
//					BookChapter bookChapter = new BookChapter();
//					// 获得章节路径
//					String chapterHref = zhManifestMap.get(zhSpineList.get(i));
//					String chapterPath = zhZipedPath + "OEBPS/" + chapterHref;
//					Logger.d("wzp: chapterPath：%s%n" + chapterPath);
//
//					String data;
////						byte[] bytes =
////								FileHelper.inputToByte(EpubKernel.doEncrypt(
////								chapterPath, "52329717a4b326d373384c6b8b1bb02b"));
//					data = new String(tmpConsumer.desDecryption(chapterPath, SERTKEY), CharSet.UTF_8);
//					data = data.replaceAll("<font face=楷体_GB2312>", "");
//					data = data.replace("<html>\n" +
//							"<head>\n" +
//							"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n" +
//							"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
//							"</head>", "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
//							"<head>\n" +
//							"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/>\n" +
//							"<link href=\"stylesheet.css\" type=\"text/css\" rel=\"stylesheet\"/>\n" +
//							"</head>");
//					data = data.replaceAll("<font.*?>", "");
//					data = data.replaceAll("</font>", "");
//					Logger.e("wzp 中文加密前的内容：%s%n",data);
//					tmpEpubProducer.produceWithEncryption(chapterPath, data);
//
//					bookChapter.setChapterSrc("file:///" + chapterPath);
//					// 解析名称
//					bookChapter.setEnglishChapterName(zhTocList.get(i).getName());
//					chapterNameList[i] = zhTocList.get(i).getName();
//					// 解析章节id
//					int beginIndex = chapterHref.indexOf(".") + 1;
//					int endIndex = chapterHref.lastIndexOf(".");
//					String strChapterId = chapterHref.substring(beginIndex, endIndex);
//					int chapterId = Integer.parseInt(strChapterId);
//					chapterIdList[i] = chapterId;
//					bookChapter.setChapterId(chapterId);
//					if (i == 0) {
//						int segIdBegin = data.indexOf("<p id=\"");
//						String segIdStr = data.substring(segIdBegin + 7);
//						int segIdEnd = segIdStr.indexOf("\">");
//						String segId = segIdStr.substring(0, segIdEnd);
//						int segmengId = Integer.parseInt(segId);
//						bookIdInfoZh.setSegmentId(segmengId);
//						bookInfo.setMinChapterId(chapterId);
//					}
//					//bookChapter.setChapterId(i);
//					zhChapters.add(bookChapter);
//				}
//				Gson gson = new Gson();
//				String ChapterIds = gson.toJson(chapterIdList);
//				String ChapterNames = gson.toJson(chapterNameList);
//				bookIdInfoZh.setChapterId(ChapterIds);
//				bookIdInfoZh.setEnglishChapterName(ChapterNames);
//				msgbookinfo.setBookInfoZh(bookIdInfoZh);
//
//				bookInfo.setZhChaptersList(zhChapters);
//				FileHelper.zipFile(destinationPath + _bookName, _bookName + ".zh", zhEpubDir);
//			}
//			Logger.e("wzp 结束了！");
//
//			msgbookinfo.setBookId(Integer.parseInt(_bookName));
//			saveBookInfo(msgbookinfo,supportLanguage);
			// TODO: 2017/5/15
//			switch (supportLanguage) {
//				case LanguageType.ZH:
//					FileHelper.deleteDirectory(destinationPath + _bookName + "/" + _bookName + ".zh");
//					break;
//				case LanguageType.EN:
//					FileHelper.deleteDirectory(destinationPath + _bookName + "/" + _bookName + ".en");
//					break;
//				case LanguageType.ZH_AND_EN:
//					FileHelper.deleteDirectory(destinationPath + _bookName + "/" + _bookName + ".zh");
//					FileHelper.deleteDirectory(destinationPath + _bookName + "/" + _bookName + ".en");
//					break;
//			}
//			FileHelper.deleteDirectory(originalUnZipedPath);
//			updateBookDatabase(mBookId, supportLanguage);
		} catch (Exception e) {
//			mHandler.sendEmptyMessage(6);
			e.printStackTrace();
			isSuccessful = false;
			mMessage = e.getMessage();
			if(supportLanguage != -1)
				mProcessor.saveEpubEncryInfo(EpubState.DESFailure,mBookId,supportLanguage);
			Logger.e("wzp 异常：%s%n",e.getMessage());
		}
		return null;
	}

	/**
	 * 由于从云书架下载书没有附带支持语言版本的信息，
	 * 所以需要更新本地数据中的book表中该字段的信息，
	 * 这样才能成功打开书
	 *
	 * @param bookId
	 * @param supportLanguage 1：中文，2:英文，3:双语
	 */
	private void updateBookDatabase(long bookId, int supportLanguage) {

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Logger.v(TAG, "starting parse epub......");
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mProcessor.finish(isSuccessful,mBookId, TextUtils.isEmpty(mMessage)?"":mMessage);
		Logger.v(TAG, " epub parse finished......");
	}
}
