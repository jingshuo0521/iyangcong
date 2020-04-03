package com.iyangcong.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.NoteDao;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.GetRequest;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ljw on 2017/4/11.
 */

public class NoteFragment extends BaseFragment implements IBookCollection.Listener<Book> {

    @BindView(R.id.lv_note)
    ListView lvNote;

    private static final int OPEN_ITEM_ID = 0;
    private static final int EDIT_ITEM_ID = 1;
    private static final int DELETE_ITEM_ID = 2;

    private volatile BookmarksAdapter bookmarksAdapter;
    private volatile Bookmark myBookmark;
    private volatile Book myBook;
    private NoteDao noteDao;
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

        noteDao = new NoteDao(DatabaseHelper.getHelper(mContext));
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

                bookmarksAdapter = new BookmarksAdapter(lvNote, myBookmark != null);
                myCollection.addListener(NoteFragment.this);
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
        StackTraceElement[] tmpElements = Thread.currentThread().getStackTrace();
        for(StackTraceElement tmpElement:tmpElements){
            Logger.e("wzp element className:%s%n methodName:%s%n lineNumber:%d%n fileName:%s%n",tmpElement.getClassName(),tmpElement.getMethodName(),tmpElement.getLineNumber(),tmpElement.getFileName());
        }
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
                    }
                    for (BookmarkQuery query = new BookmarkQuery(book, 50); ; query = query.next()) {
                        final List<Bookmark> loaded = myCollection.bookmarks(query);
                        if (loaded.isEmpty()) {
                            break;
                        }
                        for (Bookmark b : loaded) {
                            Logger.e("wzp loadedBookmark:%s Type:%d",b.getOriginalText(),b.getStyleId());
                            if(b.getStyleId() != BookmarkType.Note)
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
            String tag = "wzp bookmark paragraphIndex：%d%n";
            Logger.e(tag,bookmark.getParagraphIndex());
            switch (item.getItemId()) {
                case OPEN_ITEM_ID:
                    gotoBookmark(bookmark);
                    return true;
                case EDIT_ITEM_ID:
                    final Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                    intent.putExtra("isCreate", false);
                    FBReaderIntents.putBookmarkExtra(intent, bookmark);
                    OrientationUtil.startActivity(getActivity(), intent);
                    return true;
                case DELETE_ITEM_ID:
                    delBookNote(bookmark);
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
                            b0.getTimestamp(Bookmark.DateType.Latest).equals(b1.getTimestamp(Bookmark.DateType.Latest));
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
                menu.add(0, OPEN_ITEM_ID, 0, "打开笔记");
                menu.add(0, EDIT_ITEM_ID, 0, "编辑笔记");
                menu.add(0, DELETE_ITEM_ID, 0, "删除笔记");
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.booknote_item, parent, false);
            final ImageView imageView = ViewUtil.findImageView(view, R.id.bookmark_item_icon);
            final TextView textView = ViewUtil.findTextView(view, R.id.bookmark_item_text);
            final TextView bookTitleView = ViewUtil.findTextView(view, R.id.bookmark_item_booktitle);
            final TextView bookNote = ViewUtil.findTextView(view, R.id.bookmark_note);
            final TextView bookTitle = ViewUtil.findTextView(view,R.id.bookmark_booktitle);
            final Bookmark bookmark = getItem(position);
            String regexp = "'";
            String text = bookmark.getText().replaceAll(regexp, "''");
            List<BookNote> note = noteDao.queryByColumn("note", text);
            if (bookmark == null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_list_plus);
                textView.setText("新建笔记");
                bookTitleView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.GONE);
                if (note.size()!=0&&note!=null){
                    bookTitle.setText("章节：" + BookInfoUtils.getChapterNameByAbsChapterId(note.get(0).getChapterId(),note.get(0).getLanguage(),mContext).replaceAll("#",""));
                }
                textView.setText(bookmark.getText());
                bookNote.setText(bookmark.getOriginalText());
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
//            if (myShowAddBookmarkItem) {
//                --position;
//            }
//            return position >= 0 ? myBookmarksList.get(position) : null;
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
                myCollection.saveBookmark(myBookmark);
            }
        }
    }

    private final Object myBookmarksLock = new Object();

    private void loadBookmarks() {
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
//                    long truelyBookId = getBookTureId(myBook);
//                    if(truelyBookId != -1){
//                        String bookPath = myBook.getPath();
//                        String bookTitle = myBook.getTitle();
//                        String bookEncoding = myBook.getEncodingNoDetection();
//                        String bookLanguage = myBook.getLanguage();
//                        myBook  = new Book(truelyBookId,bookPath,bookTitle,bookEncoding,bookLanguage);
//                    }
                    for (BookmarkQuery query = new BookmarkQuery(myBook, 50); ; query = query.next()) {
                        final List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
                        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
                        if (thisBookBookmarks.isEmpty()) {
                            break;
                        }
                        for (Bookmark bookmark : thisBookBookmarks) {
                            if (bookmark.getOriginalText() == null || !bookmark.getOriginalText().equals("i_am_a_bookmark")) {
                                bookmarks.add(bookmark);
                            }
                        }
                        bookmarksAdapter.addAll(bookmarks);
                    }
                }
            }
        }).start();
    }

    private void gotoBookmark(Bookmark bookmark) {
        bookmark.markAsAccessed();
//        myCollection.saveBookmark(bookmark);
        final Book book = myCollection.getBookById(bookmark.BookId);
        if (book != null) {
            FBReader.openBookActivity(getActivity(), book, bookmark);
        } else {
            UIMessageUtil.showErrorMessage(getActivity(), "cannotOpenBook");
        }
    }

    public void delBookNote(final Bookmark bookmark) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        List<BookNote> willDelNote;
        BookNote delNote = new BookNote();
        willDelNote = noteDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        for (BookNote note : willDelNote) {
            if (note.getNoteComment().equals(bookmark.getOriginalText()) && note.getNote().equals(bookmark.getText())) {
                delNote = note;
            }
        }
        final BookNote delNotefinal = delNote;
        GetRequest delReq = getDelNoteRequest(delNotefinal);
        if (delReq != null){
            delReq.execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    Log.e("delnote success: ",s);
                    noteDao.delete(delNotefinal);
                }

                @Override
                public void onError(Call call, Response
                        response, Exception e) {
                    Log.e("delnote fail: ",response.message());
                }
            });
        }else {
            noteDao.delete(delNotefinal);
        }
//            OkGo.get(Urls.FBReaderDelBookNoteURL)
//                    .tag(this)
//                    .params("commentsId", delNote.getCommentId() + "")
//                    .params("stuCommentsId", delNote.getStuCommentsId() == 0 ? -1 : delNote.getStuCommentsId())
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onSuccess(String s, Call call, Response response) {
//                        }
//
//                        @Override
//                        public void onError(Call call, Response response, Exception e) {
//                        }
//                    });
    }

    /**
     * 根据id和mid判断返回对应的删除请求
     * @param delNote
     *  commentId  笔记id（删除公开笔记时传，删私密笔记时不传）
     *  m_id       笔记的m_id（删除私密笔记时传，删公开笔记时不传）
     *  iscommon   是否为公开笔记  1：公开   0：私密
     * @return
     */
    private GetRequest getDelNoteRequest(BookNote delNote){
        if (delNote.getIscommon() == 1) {
            return OkGo.get(Urls.FBReaderDelBookNoteNewURL)
                    .tag(this)
                    .params("commentId", delNote.getStuCommentsId())
                    .params("m_id", delNote.getCommentId())
                    .params("iscommon", delNote.getIscommon());
        }else if(delNote.getIscommon() == 0){
            return OkGo.get(Urls.FBReaderDelBookNoteNewURL)
                    .tag(this)
                    .params("m_id", delNote.getCommentId())
                    .params("iscommon", delNote.getIscommon());
        }
        return null;
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
