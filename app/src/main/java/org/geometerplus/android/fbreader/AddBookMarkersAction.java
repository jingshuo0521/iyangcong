package org.geometerplus.android.fbreader;

import android.widget.Toast;

import com.iyangcong.reader.bean.BookMarker;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.MarkerDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

import java.util.Iterator;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ljw on 2017/4/19.
 */

class AddBookMarkersAction extends FBAndroidAction {
    private MarkerDao markerDao;

    AddBookMarkersAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
        markerDao = new MarkerDao(DatabaseHelper.getHelper(baseActivity));
    }

    @Override
    protected void run(Object... params) {
        Bookmark myBookmark = Reader.getBookmark(80, true, "i_am_a_bookmark");
//        ToastCompat.makeText(AddBookMarkersAction.super.BaseActivity, "添加书签成功", Toast.LENGTH_SHORT).show();
        uploadBookmark(myBookmark);
//        String str = Reader.getCurrentBook().getUid();
//        AndroidBookMarkUtil.uploadBookmark(myBookmark,str);
    }

    private void uploadBookmark(final Bookmark myBookmark) {
        if (!CommonUtil.getLoginState()) {
            Logger.e("wzp 在线添加笔记前请先登录");
            return;
        }
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        long userId = sp.getLong(SharedPreferenceUtil.USER_ID, 0);
        long bookId = sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0);
        int langType = sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0);
        final BookMarker bookMarker = new BookMarker();
        List<BookMarker> willDelMarker = markerDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        for (BookMarker mBookMarker : willDelMarker) {
            if (mBookMarker.getSegmentNum() == myBookmark.getParagraphIndex()) {
                ToastCompat.makeText(AddBookMarkersAction.super.BaseActivity,"已添加过书签！",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Reader.addBookmarks(myBookmark);
        bookMarker.setBookId((int) bookId);
        bookMarker.setLanguage(langType);
        int readingChapterId = BookInfoUtils.getRelativeChapterId(Reader, AddBookMarkersAction.super.BaseActivity);
        long paragraphId = BookInfoUtils.getAbsoluteParagraphId(Reader,myBookmark.getParagraphIndex());
        if(paragraphId < 0){
            ToastCompat.makeText(AddBookMarkersAction.super.BaseActivity,"添加书签失败",Toast.LENGTH_SHORT).show();
            return;
        }
        Logger.e("wzp 生成书签时的一些数据  myBookmark.getParagraphIndex():%d paragrahId:%d" , myBookmark.getParagraphIndex(),paragraphId);
        OkGo.get(Urls.FBReaderAddBookmarkURL)
                .params("bookid", bookId)
                .params("chapterid", readingChapterId)
                .params("content", myBookmark.getText())
                .params("languagetype", langType)
                .params("segmentid", paragraphId)
                .params("type", "1")
                .params("userid", userId)
                .execute(new JsonCallback<IycResponse<bookmarkerID>>(AddBookMarkersAction.super.BaseActivity) {
                    @Override
                    public void onSuccess(IycResponse<bookmarkerID> bookmarkerIDIycResponse, Call call, Response response) {
                        bookMarker.setSegmentNum(myBookmark.getParagraphIndex());
                        bookMarker.setBookmarkerId(bookmarkerIDIycResponse.getData().getBookmarkid());
                        markerDao.add(bookMarker);
                        ToastCompat.makeText(AddBookMarkersAction.super.BaseActivity, bookmarkerIDIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(AddBookMarkersAction.super.BaseActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class bookmarkerID {
        private int bookmarkid;

        int getBookmarkid() {
            return bookmarkid;
        }

        public void setBookmarkId(int bookmarkid) {
            this.bookmarkid = bookmarkid;
        }
    }
}
