package com.iyangcong.reader.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.ShelfCloudActivity;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.MsgBookInfo;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.utils.CommonUtil;

import org.geometerplus.fbreader.formats.oeb.function.EpubKernel;

/**
 * Created by ljw on 2017/5/12.
 */

public class EpubDecryptHandler extends Handler {
    private Context mContext;
    private long bookId;

    public EpubDecryptHandler(Context mContext, long bookId) {
        this.mContext = mContext;
        this.bookId = bookId;
    }

    public void decryptEpub() {
        if (bookId == 53) {
            BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
            ShelfBook book = new ShelfBook();
            book.setBookName("小王子");
            book.setBookId(53);
            book.setBookType(BookType.HAS_BUY_BOOk);
            book.setBookState("未读");
            book.setTimeStamp(System.currentTimeMillis());
            book.setBookImageUrl(CommonUtil.getBooksDir() + "/image/" + 53);
            book.setSupportLanguage(3);
            book.setBookPath(CommonUtil.getBooksDir() + "53/");
            bookDao.add(book);
        }
        new EpubKernel(mContext, bookId + ".zip", this).startParseTask();
    }

    @Override
    public void handleMessage(Message msg) {
        BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(mContext));
        MsgBookInfo msgBookInfo = (MsgBookInfo) msg.obj;
        switch (msg.what) {
            case 1:
                bookInfoDao.add(msgBookInfo.getBookInfoZh());
                break;
            case 2:
                bookInfoDao.add(msgBookInfo.getBookInfoEn());
                break;
            case 3:
                bookInfoDao.add(msgBookInfo.getBookInfoEn());
                bookInfoDao.add(msgBookInfo.getBookInfoZh());
                break;
        }
        super.handleMessage(msg);
    }
}
