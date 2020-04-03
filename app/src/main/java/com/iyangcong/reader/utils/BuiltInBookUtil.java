package com.iyangcong.reader.utils;

import android.content.Context;

import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
//import com.iyangcong.reader.handler.EpubDecryptHandler;
import com.iyangcong.reader.epub.EpubProcessResult;
import com.iyangcong.reader.epub.database.EpubBookInfo;
import com.iyangcong.reader.epub.database.EpubBookInfoDao;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.iyangcong.reader.interfaceset.EpubState;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ljw on 2017/5/20.
 */

/**
 * 内置书籍生成工具
 */
public class BuiltInBookUtil {
	public static final int DefualtBookId = 53;
	public static final String DefaultBookName = "小王子";
	//    /**
//     * @param context            上下文对象
//     * @param bookDao            数据库操作工具
//     * @param epubDecryptHandler 书籍解析器
//     */
//    @SuppressWarnings("不建议使用")
//    public static void creatBuiltInBook(Context context, BookDao bookDao, EpubDecryptHandler epubDecryptHandler) {
//        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
//        if (context == null || bookDao == null || sharedPreferenceUtil.getInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, -1) != -1 || epubDecryptHandler == null) {
//            return;
//        }
//        List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", 53);
//        if (shelfBookList == null || shelfBookList.size() == 0) {
//            try {
//                if (!FileHelper.isFileExit(CommonUtil.getBooksDir() + "53.zip")) {
//                    FileHelper.copyDataBase(context, CommonUtil.getBooksDir(), "53.zip", "53.zip");
//                    FileHelper.copyDataBase(context, CommonUtil.getBooksDir() + "image/", "53", "530");
////                    epubDecryptHandler.decryptEpub();
//                    sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, 0);
//                }
//            } catch (IOException e) {
//                sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, -1);
//                e.printStackTrace();
//            }
//        }
//
//    }
	public static void createBuiltInBookInThread(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
//				creatBuiltInBook(context);
				rebuildBooks(context);
			}
		}).start();
	}

	/**
	 *	重新构建之前构建失败的图书
	 * @param context
	 */
	public static void rebuildBooks(Context context){
		EpubBookInfoDao tmpInfoDao = ((AppContext)context).getEpubSession().getEpubBookInfoDao();
		List<EpubBookInfo> tmpList = tmpInfoDao.queryBuilder().where(EpubBookInfoDao.Properties.EpubState.notEq(EpubState.AESSuccess)).list();
		Logger.e("wzp 重构建数目：%d%n",tmpList.size());
		if(tmpList == null || tmpList.isEmpty()){
			EventBus.getDefault().post(new EpubProcessResult());//让正在加载消失
			return;
		}
		for(EpubBookInfo info:tmpList){
			Logger.e("wzp 构建书籍的Id:%d%n",info.getBookId());
//			if(info.getBookId() == DefualtBookId)
//				continue;
			DESEncodeInvoker invoker = new InvokerDESServiceUitls(context);
			invoker.invokerDESEncodeService(info.getBookId());
		}

	}
	/**
	 * 查询对应的书是否在数据库中有记录
	 * @param dao
	 * @return
	 */
	private static boolean isBookExist(EpubBookInfoDao dao){
		if(dao == null)
			return false;
		List<EpubBookInfo> tmpInfoList = dao.queryBuilder().where(EpubBookInfoDao.Properties.BookId.eq(DefualtBookId)).list();
		return tmpInfoList != null && tmpInfoList.size() != 0;
	}

	/**
	 * 发生异常的时候保存异常信息
	 * @param dao
	 */
	private static void saveExcetionInfo(EpubBookInfoDao dao){
		if(dao != null){
			List<EpubBookInfo> tmpInfoList = dao.queryBuilder().where(EpubBookInfoDao.Properties.BookId.eq(DefualtBookId)).list();
			if(tmpInfoList != null){
				for(EpubBookInfo info:tmpInfoList){
					info.setEpubState(EpubState.Exception);
					dao.update(info);
				}
			}
		}
	}

	@SuppressWarnings("目前默认不自动构建默认书籍")
	public static void creatBuiltInBook(Context context) {
		if (context == null) {
			return;
		}
		EpubBookInfoDao tmpEpubBookInfoDao = null;
		try {
			tmpEpubBookInfoDao = ((AppContext)context).getEpubSession().getEpubBookInfoDao();
			boolean need = !isBookExist(tmpEpubBookInfoDao);
			Logger.e("wzp 小王子是否需要重新构建："+need);
			if(need){
				FileHelper.copyDataBase(context, CommonUtil.getBooksDir(), DefualtBookId+".zip", DefualtBookId+".zip");
				FileHelper.copyDataBase(context, CommonUtil.getBooksDir() + "image/", DefualtBookId+"", DefualtBookId+"0");
				DESEncodeInvoker invoker = new InvokerDESServiceUitls(context);
				invoker.invokerDESEncodeService(DefualtBookId);
			}
			saveDefaultBookInfo(context,DefualtBookId);
		} catch (Exception e) {
			//如果发生了异常的话需要保存相关信息；
			saveExcetionInfo(tmpEpubBookInfoDao);
			e.printStackTrace();
		}
	}

	/**
	 * 保存默认构建的书籍的信息
	 * @param context
	 * @param bookId
	 */
	public static void saveDefaultBookInfo(Context context, int bookId) {
		BookDao bookDao = new BookDao(DatabaseHelper.getHelper(context));
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
		boolean needSave = (bookId == DefualtBookId)&&(context != null)&&(shelfBookList == null || 0 == shelfBookList.size());
		if (needSave) {
			ShelfBook book = new ShelfBook();
			book.setBookName(DefaultBookName);
			book.setBookId(DefualtBookId);
			book.setBookType(BookType.TRY_READ_BOOK);
			book.setBookState("未读");
			book.setTimeStamp(System.currentTimeMillis());
			book.setBookImageUrl(CommonUtil.getBooksDir() + "/image/" + DefualtBookId);
			book.setSupportLanguage(3);
			book.setBookPath(CommonUtil.getBooksDir() + DefualtBookId+"/");
			book.setUserId(CommonUtil.getUserId());
			bookDao.add(book);
		}
	}

}
