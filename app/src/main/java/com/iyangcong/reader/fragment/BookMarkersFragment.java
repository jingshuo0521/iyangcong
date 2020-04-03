package com.iyangcong.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.BookMarker;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.MarkerDao;
import com.iyangcong.reader.database.dao.NoteDao;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.bookmark.EditNoteActivity;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.OrientationUtil;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.book.BookmarkType;
import org.geometerplus.fbreader.book.HighlightingStyle;
import org.geometerplus.fbreader.book.IBookCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;
import yuku.ambilwarna.widget.AmbilWarnaPrefWidgetView;

import static org.geometerplus.fbreader.book.Bookmark.DateType.Latest;

/**
 * Created by ljw on 2017/6/9.
 */

public class BookMarkersFragment extends BaseFragment implements IBookCollection.Listener<Book> {

    @BindView(R.id.lv_note)
    ListView lvNote;

    private static final int OPEN_ITEM_ID = 3;
//    private static final int EDIT_ITEM_ID = 1;
    private static final int DELETE_ITEM_ID = 4;

    private volatile BookMarkersFragment.BookmarksAdapter bookmarksAdapter;
    private volatile Bookmark myBookmark;
    private volatile Book myBook;
    private MarkerDao markerDao;
    private BookDao bookDao;
    private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();

    private final BookCollectionShadow myCollection = new BookCollectionShadow();

    private final Map<Integer, HighlightingStyle> myStyles =
            Collections.synchronizedMap(new HashMap<Integer, HighlightingStyle>());

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myCollection != null) {
            myCollection.unbind();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, view);

        markerDao = new MarkerDao(DatabaseHelper.getHelper(mContext));
        bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        myBook = FBReaderIntents.getBookExtra(getActivity().getIntent(), myCollection);
        if (myBook == null) {
            getActivity().finish();
        }
        myBookmark = FBReaderIntents.getBookmarkExtra(getActivity().getIntent());

//        loadBookmarks();

//        bookmarksAdapter = new BookmarksAdapter(lvNote, myBookmark != null);

        myCollection.bindToService(getActivity(), new Runnable() {
            public void run() {

                bookmarksAdapter = new BookMarkersFragment.BookmarksAdapter(lvNote, myBookmark != null);
                myCollection.addListener(BookMarkersFragment.this);
                loadBookmarks();
            }
        });

        return view;
    }

    @Override
    protected void initData() {

    }

    // method from IBookCollection.Listener
    public void onBookEvent(BookEvent event, Book book) {
        switch (event) {
            default:
                break;
            case BookmarkStyleChanged:
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        updateStyles();
                        bookmarksAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case BookmarksUpdated:
                updateBookmarks(book);
                break;
        }
    }

    private void updateBookmarks(final Book book) {
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    final boolean flagThisBookTab = book.getId() == myBook.getId();

                    final Map<String, Bookmark> oldBookmarks = new HashMap<String, Bookmark>();
                    if (flagThisBookTab) {
                        for (Bookmark b : bookmarksAdapter.bookmarks()) {
                            oldBookmarks.put(b.Uid, b);
                        }
                    } else {
                    }
                    for (BookmarkQuery query = new BookmarkQuery(book, 50); ; query = query.next()) {
                        final List<Bookmark> loaded = myCollection.bookmarks(query);
                        if (loaded.isEmpty()) {
                            break;
                        }
                        for (Bookmark b : loaded) {
                            Logger.e("wzp loadedBookmark:%s Type:%d",b.getOriginalText(),b.getStyleId());
                            if(b.getStyleId() != BookmarkType.BookMark)
                                continue;
                            final Bookmark old = oldBookmarks.remove(b.Uid);
                            if (flagThisBookTab) {
                                bookmarksAdapter.replace(old, b);
                            }
                        }
                    }
                    if (flagThisBookTab) {
                        bookmarksAdapter.removeAll(oldBookmarks.values());
                    }
                }
            }
        }).start();
    }

    private void updateStyles() {
        synchronized (myStyles) {
            myStyles.clear();
            for (HighlightingStyle style : myCollection.highlightingStyles()) {
                myStyles.put(style.Id, style);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (bookmarksAdapter.getItem(position) != null) {
            final Bookmark bookmark = bookmarksAdapter.getItem(position);
            switch (item.getItemId()) {
                case OPEN_ITEM_ID:
                    gotoBookmark(bookmark);
                    return true;
//                case EDIT_ITEM_ID:
//                    final Intent intent = new Intent(getActivity(), EditNoteActivity.class);
//                    FBReaderIntents.putBookmarkExtra(intent, bookmark);
//                    OrientationUtil.startActivity(getActivity(), intent);
//                    return true;
                case DELETE_ITEM_ID:
                    delBookmark(bookmark);
//                    AndroidBookMarkUtil.deleteBookmark(bookmark.Uid);
                    myCollection.deleteBookmark(bookmark);
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    // method from IBookCollection.Listener
    public void onBuildEvent(IBookCollection.Status status) {
    }

    private final class BookmarksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {
        private final List<Bookmark> myBookmarksList =
                Collections.synchronizedList(new LinkedList<Bookmark>());
        private volatile boolean myShowAddBookmarkItem;

        BookmarksAdapter(ListView listView, boolean showAddBookmarkItem) {
            myShowAddBookmarkItem = showAddBookmarkItem;
            listView.setAdapter(this);
            listView.setOnItemClickListener(this);
            listView.setOnCreateContextMenuListener(this);
        }

        public List<Bookmark> bookmarks() {
            return Collections.unmodifiableList(myBookmarksList);
        }

        public void addAll(final List<Bookmark> bookmarks) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        for (Bookmark b : bookmarks) {
                            final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                            if (position < 0) {
                                myBookmarksList.add(-position - 1, b);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        private boolean areEqualsForView(Bookmark b0, Bookmark b1) {
            return
                    b0.getStyleId() == b1.getStyleId() &&
                            b0.getText().equals(b1.getText()) &&
                            b0.getTimestamp(Latest).equals(b1.getTimestamp(Latest));
        }

        public void replace(final Bookmark old, final Bookmark b) {
            if (old != null && areEqualsForView(old, b)) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        if (old != null) {
                            myBookmarksList.remove(old);
                        }
                        final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                        if (position < 0) {
                            myBookmarksList.add(-position - 1, b);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void removeAll(final Collection<Bookmark> bookmarks) {
            if (bookmarks.isEmpty()) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarksList.removeAll(bookmarks);
                    notifyDataSetChanged();
                }
            });
        }

        public void clear() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarksList.clear();
                    notifyDataSetChanged();
                }
            });
        }

        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            if (getItem(position) != null) {
                menu.add(0, OPEN_ITEM_ID, 0, "打开书签");
                menu.add(0, DELETE_ITEM_ID, 0, "删除书签");
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
            final TextView textView = ViewUtil.findTextView(view, R.id.bookmark_item_text);
            final TextView bookTitleView = ViewUtil.findTextView(view, R.id.bookmark_item_booktitle);
            final TextView bookTitle = ViewUtil.findTextView(view,R.id.bookmark_booktitle);



            final Bookmark bookmark = getItem(position);
            if (bookmark == null) {
                textView.setText("新建笔记");
                bookTitleView.setVisibility(View.GONE);
                bookTitle.setVisibility(View.GONE);
            } else {
                textView.setText(bookmark.getText());
                bookTitle.setText(DateUtils.timeStamp2Date(bookmark.getTimestamp(Latest),null) + "");
                if (myShowAddBookmarkItem) {
                    bookTitleView.setVisibility(View.GONE);
                } else {
                    bookTitleView.setVisibility(View.VISIBLE);
                    bookTitleView.setText(bookmark.BookTitle);
                }
            }
            return view;
        }

        @Override
        public final boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public final boolean isEnabled(int position) {
            return true;
        }

        @Override
        public final long getItemId(int position) {
            final Bookmark item = getItem(position);
            return item.getId();
        }

        @Override
        public final Bookmark getItem(int position) {
            if (position > myBookmarksList.size() - 1) {
                return null;
            }
            return myBookmarksList.get(position);
        }

        @Override
        public final int getCount() {
            return myBookmarksList.size();
        }

        public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Bookmark bookmark = getItem(position);
            if (bookmark != null) {
                gotoBookmark(bookmark);
            } else if (myShowAddBookmarkItem) {
                myShowAddBookmarkItem = false;
//                myCollection.saveBookmark(myBookmark);
            }
        }
    }

    private final Object myBookmarksLock = new Object();

    private void loadBookmarks() {
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    for (BookmarkQuery query = new BookmarkQuery(myBook, 50); ; query = query.next()) {
                        List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
                        if (thisBookBookmarks == null || thisBookBookmarks.isEmpty()) {
                            break;
                        }
                        Iterator<Bookmark> tmpBookmarkIterator = thisBookBookmarks.iterator();
                        for(;tmpBookmarkIterator.hasNext();){
                            Bookmark tmpBookmark = tmpBookmarkIterator.next();
                            if(tmpBookmark != null && tmpBookmark.getStyleId() != BookmarkType.BookMark)
                                tmpBookmarkIterator.remove();
                        }
                        bookmarksAdapter.addAll(thisBookBookmarks);
                    }
                }
            }
        }).start();
    }

    private void gotoBookmark(Bookmark bookmark) {
//        bookmark.markAsAccessed();
//        myCollection.saveBookmark(bookmark);
        final Book book = myCollection.getBookById(bookmark.BookId);
        if (book != null) {
            FBReader.openBookActivity(getActivity(), book, bookmark);
        } else {
            UIMessageUtil.showErrorMessage(getActivity(), "cannotOpenBook");
        }
    }

    public void delBookmark(final Bookmark bookmark) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        List<BookMarker> willDelMarker;
        BookMarker delmarker = new BookMarker();
        willDelMarker = markerDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        for (BookMarker bookMarker : willDelMarker) {
            if (bookMarker.getSegmentNum() == bookmark.getParagraphIndex()) {
                delmarker = bookMarker;
            }
        }
        markerDao.delete(delmarker);
        if (delmarker.getBookId() == 0)
            return;
        OkGo.get(Urls.FBReaderDelBookmarkURL)
                .tag(this)
                .params("bookmarkid", delmarker.getBookmarkerId() + "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ToastCompat.makeText(mContext, "删除云书签成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(mContext, "删除云书签失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取书籍在后台系统中的真实的id
     *
     * @param book
     * @return
     */
    private long getBookTureId(Book book) {
        if (book != null) {
            List<ShelfBook> bookList = bookDao.queryByColumn("bookName", book.getTitle());
            for (ShelfBook b : bookList) {
                if (b.getBookName().equals(book.getTitle())) {
                    return b.getBookId();
                }
            }
        }
        return -1;
    }

}