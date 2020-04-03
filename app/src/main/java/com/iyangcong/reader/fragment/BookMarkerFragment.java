package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BookMarkerAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.BookMarker;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.MarkerDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.AndroidBookMarkUtil;
import com.iyangcong.reader.utils.PatternUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.book.BookmarkType;
import org.geometerplus.fbreader.book.IBookCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by ljw on 2017/4/11.
 */

public class BookMarkerFragment extends BaseFragment implements IBookCollection.Listener<Book> {

    @BindView(R.id.lv_book_marker)
    ListView lvBookMarker;
    private volatile Bookmark myBookmark;
    private volatile Book myBook;
    private MarkerDao markerDao;
    private BookDao bookDao;
    private List<Bookmark> myBookmarksList = Collections.synchronizedList(new LinkedList<Bookmark>());

    private final BookCollectionShadow myCollection = new BookCollectionShadow();

    private BookMarkerAdapter bookMarkerAdapter;

    private int markerPosition;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myCollection != null){
            myCollection.unbind();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(myBookmarksList != null){
            myBookmarksList.clear();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_marker, container, false);
        ButterKnife.bind(this, view);

        markerDao = new MarkerDao(DatabaseHelper.getHelper(mContext));
        bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        if(myBookmarksList != null){
            myBookmarksList.clear();
        }
        myCollection.bindToService(getActivity(), new Runnable() {
            public void run() {

                bookMarkerAdapter = new BookMarkerAdapter(mContext, myBookmarksList);
                myCollection.addListener(BookMarkerFragment.this);
                lvBookMarker.setAdapter(bookMarkerAdapter);
                bookMarkerAdapter.notifyDataSetChanged();
                loadBookmarks();
            }
        });

        lvBookMarker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bookmark bookmark = myBookmarksList.get(i);
                gotoBookmark(bookmark);
            }
        });
        lvBookMarker.setOnCreateContextMenuListener(this);

        return view;
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        markerPosition = position;
        menu.add(0, 2, 0, "删除书签");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Bookmark bookmark = myBookmarksList.get(markerPosition);
        delBookmark(bookmark);
        AndroidBookMarkUtil.deleteBookmark(bookmark.Uid);
        myBookmarksList.remove(markerPosition);
        myCollection.deleteBookmark(bookmark);
        bookMarkerAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initData() {
        myBook = FBReaderIntents.getBookExtra(getActivity().getIntent(), myCollection);
        if (myBook == null) {
            getActivity().finish();
        }
        myBookmark = FBReaderIntents.getBookmarkExtra(getActivity().getIntent());
    }

    private void loadBookmarks() {
//        long truelyBookId = getBookTureId(myBook);
//        if(truelyBookId != -1){
//            String bookPath = myBook.getPath();
//            String bookTitle = myBook.getTitle();
//            String bookEncoding = myBook.getEncodingNoDetection();
//            String bookLanguage = myBook.getLanguage();
//            myBook  = new Book(truelyBookId,bookPath,bookTitle,bookEncoding,bookLanguage);
//        }
        for (BookmarkQuery query = new BookmarkQuery(myBook, false, 50); ; query = query.next()) {
            final List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
            if (thisBookBookmarks == null ||thisBookBookmarks.isEmpty()) {
                break;
            }
//            Iterator<Bookmark> iterator = thisBookBookmarks.iterator();
//            while (iterator.hasNext()){
//                Bookmark bookmark = iterator.next();
//                if(isNull(myBook)||isNull(mContext,myBook.getLanguage())||isNull(bookmark.getText())) {
//                    continue;
//                }else if(myBook.getLanguage().equals("en")&& PatternUtils.containChinese(bookmark.getText())){
//                    iterator.remove();
//                }else if(myBook.getLanguage().equals("zh")&&PatternUtils.containLetter(bookmark.getText())){
//                    iterator.remove();
//                }
//            }
//			for(Bookmark bookmark: thisBookBookmarks){
//				if(!myBookmarksList.contains(bookmark)){
//					myBookmarksList.add(bookmark);
//				}
//			}
            myBookmarksList.addAll(thisBookBookmarks);
        }
    }

    @Override
    public void onBookEvent(BookEvent event, Book book) {

    }

    @Override
    public void onBuildEvent(IBookCollection.Status status) {

    }

    private void gotoBookmark(Bookmark bookmark) {
        bookmark.markAsAccessed();
        myCollection.saveBookmark(bookmark);
        final Book book = myCollection.getBookById(bookmark.BookId);
        if (book != null) {
            FBReader.openBookActivity(getActivity(), book, bookmark);
        } else {
            UIMessageUtil.showErrorMessage(getActivity(), "cannotOpenBook");
        }
    }

    public void delBookmark(final Bookmark bookmark){
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        List<BookMarker> willDelMarker = new ArrayList<>();
        BookMarker delmarker = new BookMarker();
        willDelMarker = markerDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        for(BookMarker bookMarker:willDelMarker){
            if(bookMarker.getSegmentNum() == bookmark.getParagraphIndex()){
                delmarker = bookMarker;
            }
        }
        markerDao.delete(delmarker);
        if(delmarker.getBookId() == 0)
            return;
        OkGo.get(Urls.FBReaderDelBookmarkURL)
                .tag(this)
                .params("bookmarkid",delmarker.getBookmarkerId()+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ToastCompat.makeText(mContext,"删除云书签成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(mContext,"删除云书签失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private long getBookTureId(Book book){
		if(book != null){
			List<ShelfBook> bookList = bookDao.queryByColumn("bookName",book.getTitle());
			for(ShelfBook b:bookList){
				if(b.getBookName().equals(book.getTitle())){
					return b.getBookId();
				}
			}
		}
        return -1;
    }
}
