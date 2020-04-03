package com.iyangcong.reader.epub;

import android.content.Context;
import android.text.TextUtils;

import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.epub.database.EpubBookInfo;
import com.iyangcong.reader.epub.database.EpubBookInfoDao;
import com.iyangcong.reader.interfaceset.EpubState;
import com.iyangcong.reader.interfaceset.LanguageType;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.FileHelper;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.formats.oeb.function.encryp.DESEpubConsumer;
import org.geometerplus.fbreader.formats.oeb.function.encryp.EpubProducer;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by WuZepeng on 2018-01-03.
 */

public class EpubProcessorImpl implements EpubProcessor {

	private Context mContext;

	public EpubProcessorImpl(Context context) {
		mContext = context;
	}

	@Override
	public boolean hasEpub(String desKey, long bookId) {
		//先判断本地是否有试读版本的书，如果有试读版本的书，然后本次下载的又是完整版的话，就直接覆盖。
		BookDao tmpBookDao = new BookDao(DatabaseHelper.getHelper(mContext));
		List<ShelfBook> tmpShelfBooks = tmpBookDao.queryByColumn("bookId",bookId,"bookType", BookType.HAS_BUY_BOOk);
		if(!tmpShelfBooks.isEmpty()){
				return false;
		}
//		EpubBookInfoDao tmpInfoDao = ((AppContext)mContext).getEpubSession().getEpubBookInfoDao();
//		List<EpubBookInfo> tmpInfoList = tmpInfoDao.queryBuilder().where(EpubBookInfoDao.Properties.BookId.eq(bookId)).list();
//		if(tmpInfoList != null && !tmpInfoList.isEmpty()){
//			for(EpubBookInfo tmpInfo:tmpInfoList){
//				//数据库查询到的都是AESSuccess,那么说明都已经加密过了，不用再加密了。
//				//如果数据库中查询到的记录不是AESSuccess,那么说明是失败了，还是需要加密
//				//这里之所以这么记录是因为目前没有做保存DES解密的文本，
//				// 如果整个DES解密以及文本处理、AES加密过程中发生异常的话就必须重新执行这个过程。
//				if(tmpInfo.getEpubState() != EpubState.AESSuccess){
//					return false;
//				}
//			}
//			return true;
//		}
		//如果查询到的记录为空，说明之前没有对这本书进行加密
		return false;
	}

	@Override
	public void saveEpubEncryInfo(int infoType, long bookId, int languageType) {
		EpubBookInfoDao tmpInfoDao = ((AppContext)mContext).getEpubSession().getEpubBookInfoDao();
		List<EpubBookInfo> tmpInfoList = tmpInfoDao.queryBuilder().where(EpubBookInfoDao.Properties.BookId.eq(bookId)).list();
		Logger.e("wzp tmpInfoList:%s%n",tmpInfoList.toString());
		if(tmpInfoList != null && tmpInfoList.size() > 0){
			for(EpubBookInfo tmpInfo:tmpInfoList){
				tmpInfo.setEpubState(infoType);
				tmpInfo.setBookId(bookId);
				tmpInfo.setLanguage(languageType);
				tmpInfo.setTimeStamp(new Date());
				tmpInfoDao.update(tmpInfo);
			}
		}else{
			List<EpubBookInfo> allBook = tmpInfoDao.loadAll();
			EpubBookInfo info;
			Logger.e("wzp allBook:%s%n",allBook.toString());
			if(allBook != null && !allBook.isEmpty()){
				long id = allBook.get(allBook.size()-1).getId()+1;
				info = new EpubBookInfo(id,bookId,infoType,new Date(),languageType);
			}else{
				info = new EpubBookInfo(0,bookId,infoType,new Date(),languageType);
			}
			tmpInfoDao.insert(info);
		}
	}

	@Override
	public boolean clearCache(String path) {
		return FileHelper.deleteDirectory(path);
	}

	@Override
	public void unZipFile(String originalPath, String destination, String fileName) throws IOException {
		Logger.e("wzp originalUnZipedPath:%s%ndestinationPath:%s%n_bookName:%s%n", originalPath, destination, fileName);
		FileHelper.unzip(originalPath, destination, fileName);
	}

	@Override
	public int getLangaugeType(String filePath) {
		int supportLanguage = -1;
		File file = new File(filePath);
		if (!file.exists()) {
			return supportLanguage;
		}
		if (file.isDirectory()) {
			String[] fileName = file.list();
			for (int i = 0; i < fileName.length; i++) {
				if (fileName[i].contains("en.epub")) {
					if (supportLanguage == LanguageType.ZH) {
						supportLanguage = LanguageType.ZH_AND_EN;
						break;
					} else {
						supportLanguage = LanguageType.EN;
					}
				}
				if (fileName[i].contains("zh.epub")) {
					if (supportLanguage == LanguageType.EN) {
						supportLanguage = LanguageType.ZH_AND_EN;
						break;
					} else {
						supportLanguage = LanguageType.ZH;
					}
				}
			}
		}
		return supportLanguage;
	}

	@Override
	public void handleSubEpubBylanguageType(String destinationPath, long bookId, int language, DESEpubConsumer consumer, EpubProducer epubProducer) throws Exception {
		BookInfo info = null;
		EpubLanguageTypeProcessor tmpProcessor;
		switch (language) {
			case LanguageType.ZH:
				tmpProcessor = new ZhEpubProcessor();
				info = tmpProcessor.getBookInfoFromEpub(destinationPath,bookId,consumer,epubProducer);
				saveInfo(info);
				break;
			case LanguageType.EN:
				tmpProcessor = new EnEpubProcessor();
				info = tmpProcessor.getBookInfoFromEpub(destinationPath,bookId,consumer,epubProducer);
				saveInfo(info);
				break;
			case LanguageType.ZH_AND_EN:
				tmpProcessor = new EnEpubProcessor();
				info = tmpProcessor.getBookInfoFromEpub(destinationPath,bookId,consumer,epubProducer);
				info.setLanguage(LanguageType.ZH_AND_EN);
//				saveInfo(info);
				tmpProcessor = new ZhEpubProcessor();
				BookInfo info2 = tmpProcessor.getBookInfoFromEpub(destinationPath,bookId,consumer,epubProducer);
//				info.setLanguage(LanguageType.ZH_AND_EN);
				info.setChineseChapterName(info2.getChineseChapterName());
				saveInfo(info);
				break;
		}
		saveEpubEncryInfo(EpubState.AESSuccess,bookId,language);
	}

	@Override
	public void saveInfo(BookInfo info) {
		BookInfoDao tmpDao = new BookInfoDao(DatabaseHelper.getHelper(mContext));
		List<BookInfo> tmpBookInfos = tmpDao.queryByColumn("bookId", info.getBookId());
		//如果在数据库中查不到这本书的信息，则插入
		Logger.e("wzp shelfBooK==null?:%s%n",(tmpBookInfos==null)+"");
		if(tmpBookInfos != null){
			Logger.e("wzp shelfBooK.size()=%d%n",tmpBookInfos.size());
		}
		if (tmpBookInfos == null || tmpBookInfos.size() == 0) {
			tmpDao.createOrUpdate(info);
		} else {//如果能查找到这本书在bookinfo表中的数据，则更新；
//			boolean alreadyHasChineseChapaterNames = !TextUtils.isEmpty(tmpBookInfos.get(0).getChineseChapterName());
//			boolean alreadyHasEnglishChapterNames = !TextUtils.isEmpty(tmpBookInfos.get(0).getEnglishChapterName());
//			if(alreadyHasChineseChapaterNames){
//				info.setChineseChapterName(tmpBookInfos.get(0).getChineseChapterName());
//			}
//			if(alreadyHasEnglishChapterNames){
//				info.setEnglishChapterName(tmpBookInfos.get(0).getEnglishChapterName());
//			}
			info.setId(tmpBookInfos.get(0).getId());
			tmpDao.update(info);
		}
		//刷新book表中的数据
		Logger.e("wzp supportLanguage：" + info.getLanguage());
		if (info.getLanguage() != LanguageType.ZH && info.getLanguage() != LanguageType.EN && info.getLanguage() != LanguageType.ZH_AND_EN) {
			Logger.e("wzp 没有支持的语言类型");
			return;
		}
		BookDao tmpBookDao = new BookDao(DatabaseHelper.getHelper(mContext));
		List<ShelfBook> shelfBook = tmpBookDao.queryByColumn("bookId", info.getBookId());
		Logger.e("wzp shelfBooK==null?:%s%n",(shelfBook==null)+"");
		if(shelfBook != null){
			Logger.e("wzp shelfBooK.size()=%d%n",shelfBook.size());
		}
		if (shelfBook != null && shelfBook.size() > 0) {
			Logger.e("wzp 查询到的:%d%n",shelfBook.get(0).getSupportLanguage());
			if (shelfBook.get(0).getSupportLanguage() == 0|| shelfBook.get(0).getSupportLanguage() != info.getLanguage()) {
				shelfBook.get(0).setSupportLanguage(info.getLanguage());
				tmpBookDao.update(shelfBook.get(0));
			}
		}
	}

	@Override
	public boolean deleteOriginFile(String path) {
		Logger.e("wzp 删除源文件：%s%n",path);
		return FileHelper.deleteDirectory(path);
	}

	@Override
	public void finish(boolean isSuccess, long bookId, String message) {
//		Logger.e("wzp isSuccess:%s%nbookId:%d%nmessage:%s%n",isSuccess+"",bookId,message);
//		EventBus.getDefault().post(new EpubProcessResult(bookId,isSuccess,message));
	}
}
